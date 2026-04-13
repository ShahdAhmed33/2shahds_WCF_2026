#!/bin/bash
## Script to launch the package
CLASSPATH=$(dirname ${0})
java \
-Djdk.crypto.KeyAgreement.legacyKDF=true \
-Dfile.encoding=UTF-8 \
-Dstdout.encoding=UTF-8 \
-Dstderr.encoding=UTF-8 \
-cp "${CLASSPATH}/bin/web1-1.0.jar:${CLASSPATH}/libs/*" \
-XX:+ShowCodeDetailsInExceptionMessages web1.starterclass ${@}
