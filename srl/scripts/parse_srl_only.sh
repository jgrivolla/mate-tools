#!/bin/sh

## There are three sets of options that need, may need to, and could be changed.
## (1) deals with input and output. You have to set these (in particular, you need to provide a training corpus)
## (2) deals with the jvm parameters and may need to be changed
## (3) deals with the behaviour of the system

## For further information on switches, see the source code, or run
## java -cp srl.jar se.lth.cs.srl.Parse --help

##################################################
## (1) The following needs to be set appropriately
##################################################
input="/home/anders/corpora/conll09/eng/CoNLL2009-evaluation-English-SRLonly.txt" #evaluation corpus
lang="eng"
model="models/eng/srl-eng.model"
output="eng.out"

##################################################
## (2) These ones may need to be changed
##################################################
JAVA="java" #Edit this i you want to use a specific java binary.
MEM="2g" #Memory for the JVM, might need to be increased for large corpora.
CP="srl.jar:lib/liblinear-1.51-with-deps.jar"
JVM_ARGS="-cp $CP -Xmx$MEM"

##################################################
## (3) The following changes the behaviour of the system
##################################################
#RERANKER="-reranker" #Uncomment this if you want to use a reranker too. The model is assumed to contain a reranker. While training, this has to be set appropriately.
#NOPI="-nopi" #Uncomment this if you want to skip the predicate identification step. This setting is equivalent to the CoNLL 2009 ST.


CMD="$JAVA $JVM_ARGS se.lth.cs.srl.Parse $lang $input $model $RERANKER $NOPI $output"
echo "Executing: $CMD"

$CMD

