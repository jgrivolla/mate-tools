#!/bin/sh

## There are three sets of options that need, may need to, and could be changed.
## (1) deals with input and output. You have to set these (in particular, you need to provide models)
## (2) deals with the jvm parameters and may need to be changed
## (3) deals with the behaviour of the system

## For further information on switches, see the source code, or run
## java -cp srl-20100902.jar se.lth.cs.srl.http.HttpPipeline

##################################################
## (1) The following needs to be set appropriately
##################################################
LANG="eng"
TOKENIZER_MODEL="models/eng/EnglishTok.bin.gz"
LEMMATIZER_MODEL="models/eng/lemma-eng.model"
POS_MODEL="models/eng/tag-eng.model"
#MORPH_MODEL="models/ger/morph-ger.model" #Morphological tagger is not applicable to English. Fix the path and uncomment if you are running german.
PARSER_MODEL="models/eng/prs-eng.model"
SRL_MODEL="models/eng/srl-eng.model"

PORT=8081 #The port to listen on

##################################################
## (2) These ones may need to be changed
##################################################
JAVA="java" #Edit this i you want to use a specific java binary.
MEM="3g" #Memory for the JVM, might need to be increased for large corpora.
CP="srl.jar:lib/anna.jar:lib/liblinear-1.51-with-deps.jar:lib/opennlp-tools-1.4.3.jar:lib/maxent-2.5.2.jar:lib/trove.jar"
JVM_ARGS="-cp $CP -Xmx$MEM"

##################################################
## (3) The following changes the behaviour of the system
##################################################
#RERANKER="-reranker" #Uncomment this if you want to use a reranker too. The model is assumed to contain a reranker. While training, the corresponding parameter has to be provided.

CMD="$JAVA $JVM_ARGS se.lth.cs.srl.http.HttpPipeline $LANG -token $TOKENIZER_MODEL -lemma $LEMMATIZER_MODEL -tagger $POS_MODEL -parser $PARSER_MODEL -srl $SRL_MODEL -port $PORT $RERANKER"
if [ "$MORPH_MODEL" != "" ]; then
  CMD="$CMD -morph $MORPH_MODEL"
fi

echo "Executing: $CMD"
$CMD