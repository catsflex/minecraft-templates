<#
.SYNOPSIS
	Renames the mod: mod ID, name, Java package, class name prefix, assets folder and mixin config.
	
.DESCRIPTION
	Reads the current values from gradle.properties, so it can be run again later to rename the mod once more.
	The class name prefix is derived from the mod ID: example-mod -> ExampleMod (ExampleModClient.java).
	
.EXAMPLE
	powershell -ExecutionPolicy Bypass -File .\rename-mod.ps1 -ModId cool-mod -Name "Cool Mod"
	
.EXAMPLE
	powershell -ExecutionPolicy Bypass -File .\rename-mod.ps1 -ModId cool-mod -Package cool -Group dev.someone
#>
param(
	[Parameter(Mandatory = $true)]
	[ValidatePattern('^[a-z][a-z0-9_-]{1,63}$')]
	[string] $ModId,
	
	[string] $Name,
	
	[ValidatePattern('^[a-z_][a-z0-9_]*$')]
	[string] $Package,
	
	[ValidatePattern('^[a-z_][a-z0-9_]*(\.[a-z_][a-z0-9_]*)*$')]
	[string] $Group,
	
	[string] $Description
)

$ErrorActionPreference = 'Stop'
$Root = $PSScriptRoot
$Utf8NoBom = New-Object System.Text.UTF8Encoding $false

function Get-FullPath([string] $RelativePath) {
	return Join-Path $Root $RelativePath
}

function Read-Text([string] $Path) {
	return [System.IO.File]::ReadAllText($Path)
}

function Write-Text([string] $Path, [string] $Text) {
	[System.IO.File]::WriteAllText($Path, $Text, $Utf8NoBom)
}

function ConvertTo-PascalCase([string] $Id) {
	$parts = $Id -split '[-_]' | Where-Object { $_ }
	return ($parts | ForEach-Object { $_.Substring(0, 1).ToUpper() + $_.Substring(1) }) -join ''
}

function ConvertTo-Title([string] $Id) {
	$parts = $Id -split '[-_]' | Where-Object { $_ }
	return ($parts | ForEach-Object { $_.Substring(0, 1).ToUpper() + $_.Substring(1) }) -join ' '
}

function Get-Property([string] $Text, [string] $Key) {
	$match = [regex]::Match($Text, "(?m)^$([regex]::Escape($Key))=([^\r\n]*)")
	if (-not $match.Success) {
		throw "Property '$Key' not found in gradle.properties"
	}
	return $match.Groups[1].Value
}

function Set-Property([string] $Text, [string] $Key, [string] $Value) {
	$pattern = "(?m)^$([regex]::Escape($Key))=[^\r\n]*"
	if (-not [regex]::IsMatch($Text, $pattern)) {
		throw "Property '$Key' not found in gradle.properties"
	}
	return [regex]::Replace($Text, $pattern, "$Key=$Value".Replace('$', '$$'))
}

function Remove-EmptyDirectories([string] $Directory, [string] $StopAt) {
	while ($Directory -and ($Directory -ne $StopAt) -and (Test-Path $Directory) -and
		-not (Get-ChildItem -Force $Directory | Select-Object -First 1)) {
		Remove-Item $Directory
		$Directory = Split-Path $Directory
	}
}

# --- Old and new values ---

$propertiesPath = Get-FullPath 'gradle.properties'
$properties = Read-Text $propertiesPath

$oldId = Get-Property $properties 'mod_id'
$oldGroup = Get-Property $properties 'mod_group'
$oldPackage = Get-Property $properties 'mod_package'
$oldName = Get-Property $properties 'mod_name'
$oldPrefix = ConvertTo-PascalCase $oldId

if (-not $Name) { $Name = ConvertTo-Title $ModId }
if (-not $Group) { $Group = $oldGroup }
if (-not $Package) { $Package = $ModId -replace '[-_]', '' }
if ($Package -notmatch '^[a-z_][a-z0-9_]*$') {
	throw "Can't derive a Java package from '$ModId', pass -Package explicitly"
}

$newPrefix = ConvertTo-PascalCase $ModId
$oldFqn = "$oldGroup.$oldPackage"
$newFqn = "$Group.$Package"

Write-Host "Mod ID:   $oldId -> $ModId"
Write-Host "Package:  $oldFqn -> $newFqn"
Write-Host "Classes:  $($oldPrefix)* -> $($newPrefix)*"
Write-Host "Name:     $oldName -> $Name"
Write-Host ''

# --- Java package directories ---

if ($oldFqn -ne $newFqn) {
	foreach ($sourceSet in 'main', 'test') {
		$javaRoot = Get-FullPath "src/$sourceSet/java"
		$oldDir = Join-Path $javaRoot ($oldFqn.Replace('.', '\'))
		$newDir = Join-Path $javaRoot ($newFqn.Replace('.', '\'))
		if (-not (Test-Path $oldDir)) { continue }
		
		if (Test-Path $newDir) {
			throw "Can't move $($oldDir): $newDir already exists"
		}
		
		New-Item -ItemType Directory -Force (Split-Path $newDir) | Out-Null
		Move-Item $oldDir $newDir
		Remove-EmptyDirectories (Split-Path $oldDir) $javaRoot
		Write-Host "Moved    src/$sourceSet/java/$($oldFqn.Replace('.', '/')) -> $($newFqn.Replace('.', '/'))"
	}
}

# --- Class files named after the mod (ExampleModClient.java) ---

if ($oldPrefix -cne $newPrefix) {
	@(Get-ChildItem (Get-FullPath 'src') -Recurse -File -Filter "*$oldPrefix*") | ForEach-Object {
		$newFileName = $_.Name.Replace($oldPrefix, $newPrefix)
		Rename-Item $_.FullName $newFileName
		Write-Host "Renamed  $($_.Name) -> $newFileName"
	}
}

# --- References inside sources and resources ---

@(Get-ChildItem (Get-FullPath 'src') -Recurse -File -Include '*.java', '*.json') | ForEach-Object {
	$text = Read-Text $_.FullName
	$newText = $text.Replace("package $oldFqn;", "package $newFqn;").Replace("$oldFqn.", "$newFqn.")
	if ($oldPrefix -cne $newPrefix) {
		$newText = $newText.Replace($oldPrefix, $newPrefix)
	}
	
	# Translations are plain JSON: mod ID in keys and the mod name in texts are written literally.
	if ($_.Directory.Name -eq 'lang') {
		$idPattern = "(?<![a-z0-9_-])$([regex]::Escape($oldId))(?![a-z0-9_-])"
		$newText = [regex]::Replace($newText, $idPattern, $ModId.Replace('$', '$$'))
		if ($oldName) {
			$newText = $newText.Replace($oldName, $Name)
		}
	}
	
	if ($newText -cne $text) {
		Write-Text $_.FullName $newText
		Write-Host "Updated  $($_.FullName.Substring($Root.Length + 1))"
	}
}

# --- Assets folders and mixin config ---

if ($oldId -ne $ModId) {
	foreach ($resourceRoot in 'src/main/resources', 'src/main/templates/resources') {
		$oldAssets = Get-FullPath "$resourceRoot/assets/$oldId"
		if (Test-Path $oldAssets) {
			Rename-Item $oldAssets $ModId
			Write-Host "Renamed  $resourceRoot/assets/$oldId -> $ModId"
		}
		
		$oldMixins = Get-FullPath "$resourceRoot/$oldId.mixins.json"
		if (Test-Path $oldMixins) {
			Rename-Item $oldMixins "$ModId.mixins.json"
			Write-Host "Renamed  $resourceRoot/$oldId.mixins.json -> $ModId.mixins.json"
		}
	}
}

# --- gradle.properties ---

$properties = Set-Property $properties 'mod_id' $ModId
$properties = Set-Property $properties 'mod_name' $Name
$properties = Set-Property $properties 'mod_group' $Group
$properties = Set-Property $properties 'mod_package' $Package
if ($Description) {
	$properties = Set-Property $properties 'mod_description' $Description
}
Write-Text $propertiesPath $properties
Write-Host 'Updated  gradle.properties'

Write-Host ''
Write-Host 'Done! Reload the Gradle project in IntelliJ IDEA.'
