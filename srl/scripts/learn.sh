#!/bin/sh

## There are three sets of options that need, may need to, and could be changed.
## (1) deals with input and output. You have to set these (in particular, you need to provide a training corpus)
## (2) deals with the jvm parameters and may need to be changed
## (3) deals with the behaviour of the system

## For further information on switches, see the source code, or run
## java -cp srl.jar se.lth.cs.srl.Learn --help

##################################################
## (1) The following needs to be set appropriately
##################################################
corpus="/home/anders/corpora/conll09/eng/CoNLL2009-ST-English-train.txt" #training corpus
lang="eng"
model="srl-eng.model"

##################################################
## (2) These ones may need to be changed
##################################################
JAVA="java" #Edit this i you want to use a specific java binary.
MEM="2500m" #Memory for the JVM, might need to be increased for large corpora.
CP="srl.jar:lib/liblinear-1.51-with-deps.jar"
JVM_ARGS="-cp $CP -Xmx$MEM"

##################################################
## (3) The following changes the behaviour of the system
##################################################
#LLBINARY="-llbinary /home/anders/liblinear-1.6/train" #Path to locally compiled liblinear. Uncomment this and correct the path if you have it. This will make training models faster (30-40%)
#RERANKER="-reranker" #Uncomment this if you want to train a reranker too. This takes about 8 times longer than the simple pipeline.


#Execute
CMD="$JAVA $JVM_ARGS se.lth.cs.srl.Learn $lang $corpus $model $RERANKER $LLBINARY"
echo "Executing: $CMD"

$CMD

