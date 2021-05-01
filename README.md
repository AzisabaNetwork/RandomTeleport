# RandomTeleport
## Overview
This is the random teleportation plugin. it's able to teleport player to the location what safety and randomly. 
## Installing
You can set up easy and quickly. It needs 3 steps.  
#### First, Downloading a jar And Install plug-in. [download link](https://github.com/AzisabaNetwork/RandomTeleport/releases/tag/1.0.0) 
#### Second, Customise configuration to meet your needs.
how to set up? I will introduce.
1. open config.yml with editor
2. check world's name to set up
3. write setting to config.yml (please follow this example)
```yaml
point:
  <point_name>:
    world: <world_name>
```
4. save and close config.yml
5. execute reload command "/randomtp reload" as Player  
#### Third, Set sign block as Teleporter  
1. set sign anywhere
2. enter '[RandomTP]' at first line
3. enter point name at second line
4. close editor  
Plugin will format it as Teleporter if it way is right.
   
## Commands
/randomtp <- show help (base command)  
/randomtp list <- display list of points
/randomtp reload <- reload configuration
## Permissions
General Permission(it needs to teleport point)
```
randomteleport.general
```
Admin Permission(it needs to reload configuration and display list of points)
```
randomteleport.admin
```
## Developer
- [testusuke](https://github.com/testusuke)
## License
Apache-2.0