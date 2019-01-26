#/bin/bash
syst=$(uname)

if [ "$syst" = Darwin ] ; then
  JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Versions/1.6/Home
  export JAVA_HOME
fi

quiet_kill() {
  killall $1 $2 1>/dev/null 2>/dev/null
}

(quiet_kill -STOP eclipse || quiet_kill -STOP java || echo non to suspend) && ant "$@" clean all ; (quiet_kill -CONT eclipse || quiet_kill -CONT java || echo non to resume)
