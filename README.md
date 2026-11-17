# Fabric Mod Template for 26.3 (Client-side)

This is a lightweight template for creating **client-side Fabric** mods.

## Checklist

Before starting work on your project, make sure to look through the files/directories below:

- `rename-mod.ps1`
	- Run it to rename mod ID, name, packages, classes, assets & mixin config:
	  `powershell -ExecutionPolicy Bypass -File .\rename-mod.ps1 -ModId my-mod -Name "My Mod"`


- `gradle.properties`
	- Fill in description, links & license


- `src/main/resources/assets/example-mod`
	- Change icon
	- Change translations
