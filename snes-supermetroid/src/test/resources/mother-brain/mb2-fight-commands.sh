function prepareDirectory {
  workingDirectory="$1";
  rng="$2";
  
  if [ "" != "$workingDirectory" -a "" != "$rng" ];
  then 
    rm -rf "$workingDirectory/$rng";
    mkdir -p "$workingDirectory/$rng/211792";
    echo -n '211791 011D 4650 '"$rng"' 0000 8EAA 0000 0000 FFD7 0000 0000 0000 003B 004A 00C3 0600' > "$workingDirectory/$rng/211792/inputs.txt";
    echo -n '' > "$workingDirectory/$rng/211792/output-input-map.txt";
  else 
    echo 'Usage: prepareDirectory <working directory path> <initial RNG value>';
  fi;
}
# prepareDirectory ~/'Desktop/sniq-100-mb2' 'B079';

function runSimulation {
  java -cp "$M2_REPO"/'ch/qos/logback/logback-classic/1.2.3/logback-classic-1.2.3.jar':"$M2_REPO"/'ch/qos/logback/logback-core/1.2.3/logback-core-1.2.3.jar':"$M2_REPO"/'log4j/log4j/1.2.17/log4j-1.2.17.jar':"$M2_REPO"/'org/slf4j/slf4j-api/1.7.25/slf4j-api-1.7.25.jar':"$M2_REPO"/'org/slf4j/slf4j-log4j12/1.7.29/slf4j-log4j12-1.7.29.jar':"$M2_REPO"/'com/github/dan-tas/tas-framework/1.0-SNAPSHOT/tas-framework-1.0-SNAPSHOT.jar':"$M2_REPO"/'com/github/dan-tas/snes-supermetroid/1.0-SNAPSHOT/snes-supermetroid-1.0-SNAPSHOT.jar' com.github.dan_tas.snes.supermetroid.enemy.motherbrain.MB2FightJobProcessor "$*";
}

# need to mvn clean install on the base project before running the simulation
# export M2_REPO='/c/Users/Dan/.m2/repository';
# runSimulation process; # simulate the MB2 fight
# runSimulation; # trace a specific output from the end of the job to the start

# Walk through the potential routes from the beginning to the end, only showing events of interest
# tac super-metroid-tas.log | sed '/Tracing /q' | sed -e 's/ # hit by CIWP shot//g' -e 's/ # do nothing for 1 frame//g' -e 's/ # choose an attack next frame//g' | grep '#';
