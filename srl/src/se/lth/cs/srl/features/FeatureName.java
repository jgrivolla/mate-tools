package se.lth.cs.srl.features;

public enum FeatureName {
	PredWord, PredLemma, PredPOS, PredDeprel, PredLemmaSense, PredFeats, 
    PredParentWord, PredParentPOS, PredParentFeats,
    DepSubCat,
	   ChildDepSet, ChildWordSet, ChildPOSSet,
	   ArgWord, ArgPOS, ArgFeats, ArgDeprel,
	   LeftWord, LeftPOS, LeftFeats,
	   RightWord, RightPOS, RightFeats,
	   LeftSiblingWord, LeftSiblingPOS, LeftSiblingFeats,
	   RightSiblingWord, RightSiblingPOS, RightSiblingFeats,
	   POSPath, DeprelPath,
	   Position, 
	   
		BrownShortPred, BrownShortPredParent, BrownShortArg, BrownShortLeftDep, BrownShortRightDep, BrownShortLeftSibling, BrownShortRightSibling,
		BrownLongPred, BrownLongPredParent, BrownLongArg, BrownLongLeftDep, BrownLongRightDep, BrownLongLeftSibling, BrownLongRightSibling,

}
