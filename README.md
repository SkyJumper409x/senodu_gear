# sendou_gear

This Java program's purpose is to use a Screenshot of *Splatoon 3*'s "Equip" Menu to detect the Abilities of the gear currently equipped, and turn them into a sendou.ink build. Copying the gear abilities over to sendou.ink by hand is something I find tedious and slow every time I have to do it, so I thought I'd give this a shot.  
I only tested 5 unique builds so far, but they appear to all be correct. (yippee :3)  
<sub>(btw, this is still a WIP)</sub>

### running this abomination

Assuming you don't have sendou.ink lying around on your computer already, the setup steps for Linux (or git bash on Windows) are:
1. ensure you are in the directory containing sendou_res_convert.sh, and that git is installed
2. run these commands  
`git clone https://github.com/sendou-ink/sendou.ink`  
`./sendou_res_convert.sh "$PWD/sendou.ink"`  
`rm -rf ./sendou.ink`
3. your taking too long (there is no third setup step)

After the setup is complete, the java program can be launched with maven, or with an ide that supports maven projects.

#### Explanation why running the script once is required:
In order to run this program, you need images of all abilities, with transparent backgrounds. There's a script (sendou_res_convert.sh) which can use the files from a (cloned) [sendou.ink](<https://github.com/sendou-ink/sendou.ink>) directory to create the ability image files. The script must be run in the directory containing it. (It's supposed to get the translations for the abilities, too, but that's bugged atm.)  


### TODO
- Actually turn the detected build into a sendou.ink link
- Support using full filepaths as command line arguments
- Clean up all the spaghetti
- Hm, I kinda like spaghetti though...


<sub>btw, I don't own skyjumper409.xyz, I just liked the package name</sub>
