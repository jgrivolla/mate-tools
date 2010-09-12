package se.lth.cs.srl.test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;

import se.lth.cs.srl.corpus.Sentence;

/**
 * @author Torbjorn Ekman (torbjorn.ekman@cs.lth.se)
 * 
 *         TestCaseOutput is an extended juint case that adds two new assert
 *         methods. These methods assert that text, supplied either as a String
 *         or in a File, has been output to standard output or standard err. The
 *         text is filtered to simplify comparison of texts using different
 *         formatting by replacing all blocks of whitespace with a single space
 *         character.
 *         
 * Edited by AB 23/7-2010. Added the sentence stuff at the bottom so all subclasses can use these sentences.
 */
public abstract class TestCaseOutput {
    private PrintStream out;
    private PrintStream err;
    private ByteArrayOutputStream baos;

    public void assertOutput(File file) {
        assertOutput("", file);
    }

    public void assertOutput(String result) {
        assertOutput("", result);
    }

    public void assertOutput(String msg, File file) {
        try {
            FileInputStream input = new FileInputStream(file);
            byte buffer[] = new byte[1024];
            StringBuffer s = new StringBuffer();
            try {
                while (input.available() != 0) {
                    int index = input.read(buffer);
                    s.append(new String(buffer, 0, index));
                }
            } catch (IOException e) {
                fail("Error reading " + file.getName());
            }
            assertOutput(msg, s.toString());
        } catch (FileNotFoundException e) {
            fail("File " + file.getName() + " not found");
        }
    }

    public void assertOutput(String msg, String result) {
        String s1 = result;//.replaceAll("\\s+", " ").trim();
        String s2 = baos.toString();//.replaceAll("\\s+", " ").trim();
        assertEquals(msg, s1, s2);
    }

    @Before
    public void setUp() throws Exception {
        out = System.out;
        err = System.err;
        baos = new ByteArrayOutputStream(1024);
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);
        System.setErr(ps);
    }

    @After
    public void tearDown() throws Exception {
        if (System.out != null && System.out != out) {
            System.setOut(out);
        }
        if (System.err != null && System.err != err) {
            System.setErr(err);
        }
    }
    
    private static final Pattern NEWLINE_PATTERN=Pattern.compile("\n");
	protected static final List<Sentence> sentences=new ArrayList<Sentence>();	
	static {
		sentences.add(Sentence.newSentence(NEWLINE_PATTERN.split(
				"1       They    they    they    PRP     PRP     _       _       2       2       SBJ     SBJ     _       _       A0\n"+
				"2       had     have    have    VBD     VBD     _       _       0       0       ROOT    ROOT    Y       have.03 _\n"+
				"3       brandy  brandy  brandy  NN      NN      _       _       2       2       OBJ     OBJ     _       _       A1\n"+
				"4       in      in      in      IN      IN      _       _       2       2       LOC     TMP     _       _       AM-LOC\n"+
				"5       the     the     the     DT      DT      _       _       6       6       NMOD    NMOD    _       _       _\n"+
				"6       library library library NN      NN      _       _       4       4       PMOD    PMOD    _       _       _\n"+
				"7       .       .       .       .       .       _       _       2       2       P       P       _       _       _\n")));
		sentences.add(Sentence.newSentence(NEWLINE_PATTERN.split(
				"1       ``      ``      ``      ``      ``      _       _       3       15      P       P       _       _       _       _       _       _\n"+
				"2       There   there   there   EX      EX      _       _       3       3       SBJ     SBJ     _       _       _       _       _       _\n"+
				"3       's      be      be      VBZ     VBZ     _       _       15      15      OBJ     OBJ     _       _       _       _       A1      _\n"+
				"4       a       a       a       DT      DT      _       _       5       5       NMOD    NMOD    _       _       _       _       _       _\n"+
				"5       possibility     possibility     possibility     NN      NN      _       _       3       3       PRD     PRD     Y       possibility.01  A2      _       _       _\n"+
				"6       of      of      of      IN      IN      _       _       5       5       NMOD    NMOD    _       _       A1      _       _       _\n"+
				"7       a       a       a       DT      DT      _       _       8       8       NMOD    NMOD    _       _       _       _       _       _\n"+
				"8       surprise        surprise        surprise        NN      NN      _       _       6       6       PMOD    PMOD    _       _       _       _       _       _\n"+
				"9       ''      ''      ''      ''      ''      _       _       8       3       P       P       _       _       _       _       _       _\n"+
				"10      in      in      in      IN      IN      _       _       8       3       LOC     LOC     _       _       _       _       _       _\n"+
				"11      the     the     the     DT      DT      _       _       13      13      NMOD    NMOD    _       _       _       _       _       _\n"+
				"12      trade   trade   trade   NN      NN      _       _       13      13      NMOD    NMOD    _       _       _       A1      _       _\n"+
				"13      report  report  report  NN      NN      _       _       10      10      PMOD    PMOD    Y       report.01       _       _       _       _\n"+
				"14      ,       ,       ,       ,       ,       _       _       15      15      P       P       _       _       _       _       _       _\n"+
				"15      said    say     say     VBD     VBD     _       _       0       0       ROOT    ROOT    Y       say.01  _       _       _       _\n"+
				"16      Michael michael michael NNP     NNP     _       _       17      17      NAME    NAME    _       _       _       _       _       _\n"+
				"17      Englund englund englund NNP     NNP     _       _       15      15      SBJ     SBJ     _       _       _       _       A0      _\n"+
				"18      ,       ,       ,       ,       ,       _       _       17      17      P       P       _       _       _       _       _       _\n"+
				"19      director        director        director        NN      NN      _       _       17      17      APPO    APPO    Y       director.01     _       _       _       A0\n"+
				"20      of      of      of      IN      IN      _       _       19      19      NMOD    NMOD    _       _       _       _       _       A1\n"+
				"21      research        research        research        NN      NN      _       _       20      20      PMOD    PMOD    _       _       _       _       _       _\n"+
				"22      at      at      at      IN      IN      _       _       19      19      NMOD    LOC     _       _       _       _       _       A2\n"+
				"23      MMS     mm      mm      NNS     NNS     _       _       22      22      PMOD    PMOD    _       _       _       _       _       _\n"+
				"24      .       .       .       .       .       _       _       15      15      P       P       _       _       _       _       _       _\n")));
		sentences.add(Sentence.newSentence(NEWLINE_PATTERN.split(
				"1	Die	der	d	ART	ART	Nom|Sg|Fem	*|*|*	2	2	NK	NK	_	_	_\n"+
				"2	Mehrheit	Mehrheit	Mehrheit	NN	NN	Nom|Sg|Fem	*|Sg|Fem	6	6	SB	SB	_	_	A1\n"+
				"3	der	der	d	ART	ART	Gen|Sg|Fem	*|*|*	5	5	NK	NK	_	_	_\n"+
				"4	britischen	britisch	britisch	ADJA	ADJA	Pos|Gen|Sg|Fem	Pos|*|*|*	5	5	NK	NK	_	_	_\n"+
				"5	Industrie	Industrie	Industrie	NN	NN	Gen|Sg|Fem	*|Sg|Fem	2	2	AG	AG	_	_	_\n"+
				"6	unterstütze	unterstützen	unterstützen	VVFIN	VVFIN	3|Sg|Pres|Subj	*|Sg|Pres|*	0	0	ROOT	ROOT	Y	unterstützen.1	_\n"+
				"7	ohnehin	ohnehin	ohnehin	ADV	ADV	_	_	6	6	MO	MO	_	_	_\n"+
				"8	den	der	d	ART	ART	Acc|Sg|Masc	*|*|*	9	9	NK	NK	_	_	_\n"+
				"9	Euro	Euro	Euro	NN	NN	Acc|Sg|Masc	*|Sg|Masc	6	6	OA	OA	_	_	A0\n"+
				"10	.	_	.	$.	$.	_	_	6	6	PUNC	PUNC	_	_	_\n")));
		sentences.add(Sentence.newSentence(NEWLINE_PATTERN.split(
				"1	Auf	auf	auf	APPR	APPR	_	_	7	7	MO	MO	_	_	_\n"+
				"2	dem	der	d	ART	ART	Dat|Sg|Masc	Dat|Sg|*	1	1	NK	NK	_	_	_\n"+
				"3	Umweg	Umweg	Umweg	NN	NN	Dat|Sg|Masc	*|Sg|Masc	1	1	NK	NK	_	_	_\n"+
				"4	über	über	über	APPR	APPR	_	_	1	1	MNR	OP	_	_	_\n"+
				"5	die	der	d	ART	ART	Acc|Pl|Fem	*|*|*	4	4	NK	NK	_	_	_\n"+
				"6	129a-Ermittlungen	129a-Ermittlung	129a-Ermittlungen	NN	NN	Acc|Pl|Fem	*|Pl|Fem	4	4	NK	NK	_	_	_\n"+
				"7	könnten	können	können	VMFIN	VMFIN	3|Pl|Past|Subj	*|Pl|Past|Subj	0	0	ROOT	ROOT	_	_	_\n"+
				"8	die	der	d	ART	ART	Nom|Pl|Fem	*|*|*	9	9	NK	NK	_	_	_\n"+
				"9	Bemühungen	Bemühung	Bemühung	NN	NN	Nom|Pl|Fem	*|*|*	7	7	SB	SB	_	_	A1\n"+
				"10	der	der	d	ART	ART	Gen|Pl|*	*|*|*	11	11	NK	NK	_	_	_\n"+
				"11	Autonomen	autonome	Autonome	NN	NN	Gen|Pl|*	*|*|*	9	9	AG	AG	_	_	_\n"+
				"12	um	um	um	APPR	APPR	_	_	9	9	OP	OP	_	_	_\n"+
				"13	ein	ein	ein	ART	ART	Acc|Sg|Fem	*|Sg|*	12	12	NK	NK	_	_	_\n"+
				"14	bißchen	bißchen	bißchen	PIAT	ADV	Acc|Sg|Fem	_	12	12	NK	NK	_	_	_\n"+
				"15	bürgerliche	bürgerlich	bürgerlich	ADJA	ADJA	Pos|Acc|Sg|Fem	Pos|*|*|*	12	12	NK	NK	_	_	_\n"+
				"16	Respektierlichkeit	Respektierlichkeit	Respektierlichkeit	NN	NN	Acc|Sg|Fem	*|Sg|Fem	12	12	NK	NK	_	_	_\n"+
				"17	im	in	im	APPRART	APPRART	Dat|Sg|Masc	Dat|Sg|*	19	19	MO	MO	_	_	_\n"+
				"18	Keim	Keim	Keim	NN	NN	Dat|Sg|Masc	*|Sg|Masc	17	17	NK	NK	_	_	_\n"+
				"19	erstickt	ersticken	ersticken	VVPP	VVPP	Psp	_	20	20	OC	OC	Y	ersticken.1	_\n"+
				"20	werden	werden	werden	VAINF	VAINF	Inf	Inf	7	7	OC	OC	_	_	_\n"+
				"21	.	_	.	$.	$.	_	_	7	7	PUNC	PUNC	_	_	_\n")));
		sentences.add(Sentence.newSentence(NEWLINE_PATTERN.split(
				"1	The	the	the	DT	DT	_	_	2	2	NMOD	NMOD	_	_	_	_	_	_\n"+
				"2	economy	economy	economy	NN	NN	_	_	4	4	NMOD	NMOD	_	_	A1	_	_	_\n"+
				"3	's	's	's	POS	POS	_	_	2	2	SUFFIX	SUFFIX	_	_	_	_	_	_\n"+
				"4	temperature	temperature	temperature	NN	NN	_	_	5	5	SBJ	SBJ	Y	temperature.01	A2	A1	_	_\n"+
				"5	will	will	will	MD	MD	_	_	0	0	ROOT	ROOT	_	_	_	AM-MOD	_	_\n"+
				"6	be	be	be	VB	VB	_	_	5	5	VC	VC	_	_	_	_	_	_\n"+
				"7	taken	take	take	VBN	VBN	_	_	6	6	VC	VC	Y	take.01	_	_	_	_\n"+
				"8	from	from	from	IN	IN	_	_	7	7	ADV	ADV	_	_	_	A2	_	_\n"+
				"9	several	several	several	DT	DT	_	_	11	11	NMOD	NMOD	_	_	_	_	_	_\n"+
				"10	vantage	vantage	vantage	NN	NN	_	_	11	11	NMOD	NMOD	_	_	_	_	A1	_\n"+
				"11	points	point	point	NNS	NNS	_	_	8	8	PMOD	PMOD	Y	point.02	_	_	_	_\n"+
				"12	this	this	this	DT	DT	_	_	13	13	NMOD	NMOD	_	_	_	_	_	_\n"+
				"13	week	week	week	NN	NN	_	_	7	7	TMP	TMP	_	_	_	AM-TMP	_	_\n"+
				"14	,	,	,	,	,	_	_	7	7	P	P	_	_	_	_	_	_\n"+
				"15	with	with	with	IN	IN	_	_	7	7	ADV	ADV	_	_	_	AM-ADV	_	_\n"+
				"16	readings	reading	reading	NNS	NNS	_	_	15	15	PMOD	PMOD	Y	reading.01	_	_	_	_\n"+
				"17	on	on	on	IN	IN	_	_	16	16	NMOD	NMOD	_	_	_	_	_	A1\n"+
				"18	trade	trade	trade	NN	NN	_	_	17	17	PMOD	PMOD	_	_	_	_	_	_\n"+
				"19	,	,	,	,	,	_	_	18	18	P	P	_	_	_	_	_	_\n"+
				"20	output	output	output	NN	NN	_	_	18	18	COORD	COORD	_	_	_	_	_	_\n"+
				"21	,	,	,	,	,	_	_	20	20	P	P	_	_	_	_	_	_\n"+
				"22	housing	housing	housing	NN	NN	_	_	20	20	COORD	COORD	_	_	_	_	_	_\n"+
				"23	and	and	and	CC	CC	_	_	22	22	COORD	COORD	_	_	_	_	_	_\n"+
				"24	inflation	inflation	inflation	NN	NN	_	_	23	23	CONJ	CONJ	_	_	_	_	_	_\n"+
				"25	.	.	.	.	.	_	_	5	5	P	P	_	_	_	_	_	_\n")));

	}
}
