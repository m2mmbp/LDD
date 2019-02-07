# LDD related scripts
## LifToObj 
LifToObj.py uses LIFExtractor.py from JrMasterModelBuilder to extract the Lego Digital Designer LIF library first and then converts the LDD .g geometry files to Alias|Wavefrom .obj files.

## ObjToRib
ObjToRib.py will read in Alias|Wavefrom .obj files, construct geometry from them and write out a Renderman .rib file for each of them.

## prman commands
```terminal
prman -d it -t:-2 ribfile.rib

oslc Primvar.osl
```

## Useful links

* https://sdm.scad.edu/faculty/mkesson/vsfx502/wip/best/spring11/miho_tomimasu/rib_archives/index.html
* RIB Entity files section: https://renderman.pixar.com/resources/RenderMan_20/ribBinding.html
* https://rmanwiki.pixar.com/display/REN/PxrSurface.mobile.phone
* http://cg.earlyworm.co.nz/renderman-commandline/
* https://renderman.pixar.com/forum/showthread.php?s=&threadid=35595&s=3f67579b2c1d88a74b98ea5f86a3c546
