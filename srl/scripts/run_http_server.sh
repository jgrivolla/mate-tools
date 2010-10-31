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
LANG="chi"
TOKENIZER_MODEL="models/chi/stanford-chinese-segmenter-2008-05-21/data"
#LEMMATIZER_MODEL="models/eng/lemma-eng.model"
POS_MODEL="models/chi/tag-chn.model"
#MORPH_MODEL="models/ger/morph-ger.model" #Morphological tagger is not applicable to English. Fix the path and uncomment if you are running german.
PARSER_MODEL="models/chi/prs-chn.model"
SRL_MODEL="models/chi/srl-chn.model"

PORT=8081 #The port to listen on

##################################################
## (2) These ones may need to be changed
##################################################
JAVA="java" #Edit this i you want to use a specific java binary.
MEM="4g" #Memory for the JVM, might need to be increased for large corpora.
CP="srl.jar:lib/anna.jar:lib/liblinear-1.51-with-deps.jar:lib/opennlp-tools-1.4.3.jar:lib/maxent-2.5.2.jar:lib/trove.jar:lib/seg.jar"
JVM_ARGS="-cp $CP -Xmx$MEM"

##################################################
## (3) The following changes the behaviour of the system
##################################################
#RERANKER="-reranker" #Uncomment this if you want to use a reranker too. The model is assumed to contain a reranker. While training, the corresponding parameter has to be provided.

CMD="$JAVA $JVM_ARGS se.lth.cs.srl.http.HttpPipeline $LANG $RERANKER -token $TOKENIZER_MODEL -tagger $POS_MODEL -parser $PARSER_MODEL -srl $SRL_MODEL -port $PORT"

if [ "$LEMMATIZER_MODEL" != ""]; then
  CMD="$CMD -lemma $LEMMATIZER_MODEL"
fi

if [ "$MORPH_MODEL" != "" ]; then
  CMD="$CMD -morph $MORPH_MODEL"
fi

echo "Executing: $CMD"
$CMD