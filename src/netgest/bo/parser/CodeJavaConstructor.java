/*Enconding=UTF-8*/
package netgest.bo.parser;

import java.util.HashMap;
import java.util.Iterator;
import netgest.bo.def.*;

import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.parser.modifiers.*;
import netgest.bo.parser.symbol.*;
import netgest.bo.parser.types.*;

import netgest.bo.parser.util.Utils;
import netgest.bo.runtime.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import netgest.bo.system.Logger;

/**
 *
 * @Company Enlace3
 * @author Francisco Câmara
 * @version 1.0
 * @since
 */
public class CodeJavaConstructor
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.parser.CodeJavaConstructor");
    
    private boDefXeoCode code;
    private String       code_string;
    private String node;
    private boDefAttribute[] attributes;
    private boDefAttribute att;
    private boDefHandler bodef;
    private ArrayList refreshAtt;
    private ArrayList formules;
    private boolean bridge;
    private String bridgeAttName;
    private ArrayList defaultValuesDependence;
    private ArrayList bridgesNames;
    private Hashtable formatts;
    private static final boolean DEBUG = false;
    private String[] codeJavaDepends;
    private boolean haveDefault = false;
    private Hashtable onChangeSubmitCode;
    private static final String TRY = "try{";
    private static final String CATCH = "}catch(Exception e){/*Ignorar*/}";

    /**
     *
     * @since
     */
    public CodeJavaConstructor()
    {
        refreshAtt = new ArrayList();
        formules = new ArrayList();
        defaultValuesDependence = new ArrayList();
        bridgesNames = new ArrayList();
        formatts = new Hashtable();
        onChangeSubmitCode = new Hashtable();
        haveDefault = false;
    }

    public static String treatCodeJava(String code)
    {
        if(code == null || code.trim().length() == 0)
            return null;
        return getCodeJava(code);
    }
    public void setCodeJavaDepends(String[] dep, boDef def, boDefAttribute at) throws boRuntimeException
    {
        if(dep != null)
        {
            this.codeJavaDepends = new String[dep.length];
            for(int i = 0; i < codeJavaDepends.length; i++)
            {
                this.codeJavaDepends[i]=reformule(dep[i], at, (boDefHandler)def);
            }
        }
        else
        {
            this.codeJavaDepends = null;
        }
    }
    
    public void resetHaveDeault()
    {
        haveDefault = false;
    }


    public void setValues(boDefHandler bodef)
    {
        this.bodef = bodef;
    }
    
    public void setValues(boDefXeoCode s, String nodeName, boDef bodef,
        boDefAttribute att, boDefAttribute[] attributes, boolean bridge,
        String bridgAttName) throws boRuntimeException
    {
        this.code = s;
        haveDefault = false;
        String nan = att.getName();
        if("defaultValue".equals(nodeName) && s.getSource() != null && s.getSource().trim().length() > 0)
        {
            haveDefault = true;
        }
        this.node = nodeName;
        
        /*
        if(DEBUG && s.getSource() != null && !"".equals(s.getSource().trim() s.trim()) && !"Y".equalsIgnoreCase(s) && !"N".equalsIgnoreCase(s))
        {
            logger.finest("Code: (" + s + ") Node: (" + nodeName + ") obj: (" 
                + bodef.getBoMasterTable() + ") Atr: (" + att.getName() + ")");
        }
        */
        
        this.attributes = attributes;
        this.att = att;
        this.bodef = (boDefHandler) bodef;
        if( s != null && s.getLanguage() == boDefXeoCode.LANG_BOL )
        {
            this.code_string = resumeCommmands(
                reformule(s.getSource(), this.att, this.bodef),
                this.att.getName(), this.bodef.getName(), formatts);
        }
        else if ( s!= null && s.getLanguage() == boDefXeoCode.LANG_JAVA )
        {
            this.code_string = s.getSource();
        }
        else
        {
            this.code_string = ""; 
        }
        this.bridge = bridge;
        this.bridgeAttName = bridgAttName;

        if (bridge && !bridgesNames.contains(bridgAttName))
        {
            bridgesNames.add(bridgAttName);
        }
    }

    private static String reformule(String s, boDefAttribute att,
        boDefHandler bodef) throws boRuntimeException
    {
        try
        {
            if ((s != null) && (s.trim().length() > 0))
            {
                s = s.trim();

                int pos = 0;
                int start = -1;
                StringBuffer sb = new StringBuffer();
                int startPar = -1;
                int endPar = -1;

                while ((start = s.indexOf("parent", pos)) != -1)
                {
                    sb = sb.delete(0, sb.length());
                    sb.append(s.substring(0, start + 6)).append("_");
                    startPar = s.indexOf("(", start) + 1;
                    endPar = s.indexOf(")", startPar);
                    sb.append(s.substring(startPar, endPar));
                    sb.append(s.substring(endPar + 1));
                    s = sb.toString();
                    pos = endPar;
                }

                sb.delete(0, sb.length());
                startPar = -1;
                endPar = -1;

                while ((start = s.indexOf("bridgeObject", pos)) != -1)
                {
                    sb = sb.delete(0, sb.length());
                    sb.append(s.substring(0, start + 12)).append("_");
                    startPar = s.indexOf("(", start) + 1;
                    endPar = s.indexOf(")", startPar);
                    sb.append(s.substring(startPar, endPar));
                    sb.append(s.substring(endPar + 1));
                    s = sb.toString();
                    pos = endPar;
                }

                s = verifyAritmDates(s, "SUM_DATE");
                s = verifyAritmDates(s, "SUBTRACT_DATE");
                s = verifyDates(s);

                return s.replaceAll("this", att.getName());
            }
        }
        catch (Exception e)
        {
            throw new boRuntimeException("XEORecognizer",MessageLocalizer.getMessage("ERROR_VERIFYING_THE_ATTRIBUTE")+
                ": " + att.getName() +
                " "+MessageLocalizer.getMessage("FROM_OBJECT")+": " + bodef.getBoName(), e);
        }

        return s;
    }

    private static String verifyDates(String s)
    {
        StringBuffer sb = new StringBuffer();
        int pos = 0;
        int start = 0;
        int end = 0;

        while ((pos = s.indexOf("DATE", pos)) != -1)
        {
            start = pos + 4;

            if (((pos - 1) < 0) || (s.charAt(pos - 1) != '_'))
            {
                sb.delete(0, sb.length());

                while (s.charAt(start) == ' ')
                {
                    start++;
                }

                if (s.charAt(start) == '(')
                {
                    sb.append("DATE").append("_");

                    ArrayList r = readDateNumbers(s, start + 1);

                    for (int i = 0; (r != null) && (i < r.size()); i++)
                    {
                        sb.append((String) r.get(i));

                        if ((i + 1) < r.size())
                        {
                            sb.append("_");
                        }
                    }

                    end = s.indexOf(")", start) + 1;
                    s = substitute(s, sb.toString(), pos, end);
                    pos = end;
                }
                else
                {
                    pos = start;
                }
            }
            else
            {
                pos = start;
            }
        }

        return s;
    }

    private static String verifyAritmDates(String s, String tolook)
    {
        //ADD_DATE | SUBTRACT_DATE
        StringBuffer sb = new StringBuffer();
        int pos = 0;
        int start = 0;
        int end = 0;

        while ((pos = s.indexOf(tolook, pos)) != -1)
        {
            sb.delete(0, sb.length());
            start = pos + tolook.length();

            while (s.charAt(start) == ' ')
            {
                start++;
            }

            if (s.charAt(start) == '(')
            {
                sb.append(tolook).append("_");

                int v = s.indexOf(",", start + 1);
                String attr = s.substring(start + 1, v).trim();
                start = v;

                ArrayList r = readDateNumbers(s, start + 1);

                for (int i = 0; (r != null) && (i < r.size()); i++)
                {
                    sb.append((String) r.get(i));

                    if ((i + 1) < r.size())
                    {
                        sb.append("_");
                    }
                }

                sb.append("(").append(attr).append(")");
                end = s.indexOf(")", start) + 1;
                s = substitute(s, sb.toString(), pos, end);
                pos = end;
            }
            else
            {
                pos = start;
            }
        }

        return s;
    }

    private static String substitute(String s, String n, int start, int end)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(s.substring(0, start));
        sb.append(n);
        sb.append(s.substring(end));

        return sb.toString();
    }

    private static ArrayList readDateNumbers(String s, int pos)
    {
        ArrayList r = new ArrayList();
        String aux = "";

        while (s.charAt(pos) != ')')
        {
            while ((s.charAt(pos) != ')') && (s.charAt(pos) != ','))
            {
                aux += s.charAt(pos);
                pos++;
            }

            r.add(aux.trim());
            aux = "";

            if (s.charAt(pos) == ',')
            {
                pos++;
            }
        }

        return r;
    }

    public String getRefreshCodeJava()
    {
        MethodDeclaration mdecl = new MethodDeclaration();
        ModifierClause mc = new ModifierClause();
        mc.setModifier(PrimitiveModifier.PUBLIC);
        mdecl.setModifiers(mc);
        mdecl.setName("onChangeSubmit");

        //mdecl.setThrow(new VarLiteral("boRuntimeException"));
        ParameterDef pdef = new ParameterDef();
        pdef.setName("attributeName");
        pdef.setType(new TypeClause("String"));

        Parameters p = new Parameters();
        ArrayList params = new ArrayList(1);
        params.add(pdef);
        p.setList(params);
        mdecl.setParameters(p);
        mdecl.setType(new TypeClause(PrimitiveType.BOOLEAN));

        ArrayList ifexps = new ArrayList();

        BlockStatement bls = new BlockStatement();

        for (int i = 0; i < refreshAtt.size(); i++)
        {
            ifexps.add(contructIf((String) refreshAtt.get(i)));
        }

        ReturnStatement r = new ReturnStatement();
        MethodInvocation mOnchange = new MethodInvocation();
        mOnchange.setInvocation(new VarLiteral("onChangeSubmitBridge"));
        EList ll = new EList();
        ArrayList rr = new ArrayList();
        rr.add(new VarLiteral("attributeName"));
        ll.setList(rr);
        mOnchange.setParameters(ll);
//        BooleanLiteral bl = new BooleanLiteral();
//        bl.setValue(false);
//        r.setReturnExp(bl);
        r.setReturnExp(mOnchange);
        ifexps.add(r);
        bls.setStatements(ifexps);
        mdecl.setCode(bls);

        return mdecl.toJAVA();
    }

    public String getDependencesFields()
    {
        MethodDeclaration mdecl = new MethodDeclaration();
        ModifierClause mc = new ModifierClause();
        mc.setModifier(PrimitiveModifier.PUBLIC);
        //mdecl.setThrow(new VarLiteral("boRuntimeException"));
        mdecl.setType(new TypeClause("String[]"));
        mdecl.setModifiers(mc);
        mdecl.setName("addDefaultDependencesFields");

        if (formules == null || formules.size() == 0 || refreshAtt.size() == 0)
        {
            ReturnStatement rs = new ReturnStatement();
            rs.setReturnExp(new VarLiteral("null"));
            ArrayList code = new ArrayList();
            code.add(rs);
            BlockStatement bl = new BlockStatement(code);
            mdecl.setCode(bl);
        }
        else
        {
            //ordenar por prioridade o cálculo das fórmulas
            Object[] objs = sort(formules);
            ArrayList code = new ArrayList();
            
            ArrayList params = new ArrayList();
            
            for (int i = 0; i < refreshAtt.size(); i++)
            {
                params.add(new StringLiteral((String) refreshAtt.get(i)));   
            }
            ConstructorExpression ce = new ConstructorExpression();
            ce.setExpression(new VarLiteral("String"));
            EList ll = new EList();
            ll.setList(params);
            
            ce.setArrayInit(ll);
            ReturnStatement r = new ReturnStatement();
            r.setReturnExp(ce);
            code.add(r);
            
            
            BlockStatement bl = new BlockStatement(code);
            mdecl.setCode(bl);
        }

        return mdecl.toJAVA();
    }

    public String getFormulaCode()
    {
        MethodDeclaration mdecl = new MethodDeclaration();
        ModifierClause mc = new ModifierClause();
        mc.setModifier(PrimitiveModifier.PUBLIC);
        mdecl.setThrow(new VarLiteral("boRuntimeException"));
        mdecl.setType(new TypeClause(PrimitiveType.VOID));
        mdecl.setModifiers(mc);
        mdecl.setName("calculateFormula");
        ParameterDef pdefFrom = new ParameterDef();
        pdefFrom.setName("from");
        pdefFrom.setType(new TypeClause("String"));
        
        ParameterDef pdefHash = new ParameterDef();
        pdefHash.setName("table");
        pdefHash.setType(new TypeClause("Hashtable"));
        
        Parameters p = new Parameters();
        ArrayList params = new ArrayList(1);
        params.add(pdefHash);
        params.add(pdefFrom);
        p.setList(params);
        mdecl.setParameters(p);

        if ((formules == null) || (formules.size() == 0))
        {
            ReturnStatement rs = new ReturnStatement();
            ArrayList code = new ArrayList();
            code.add(rs);

            BlockStatement bl = new BlockStatement(code);
            mdecl.setCode(bl);
        }
        else
        {
            //ordenar por prioridade o cálculo das fórmulas
            Object[] objs = sort(formules);
            ArrayList code = new ArrayList();   
            
            //if(p_mode != MODE_EDIT_TEMPLATE)
            
            IfStatement ifexpModeTempl = new IfStatement();
            BinaryExpression beIfexpModeTemp = new BinaryExpression();
            beIfexpModeTemp.setLeftExpression(new VarLiteral("p_mode"));
            beIfexpModeTemp.setOperator(JavaTokenTypes.NOT_EQUAL);
            beIfexpModeTemp.setRightExpression(new VarLiteral("MODE_EDIT_TEMPLATE"));
            ifexpModeTempl.setCondition(beIfexpModeTemp);            
            
            
                 
            
            //chamar as formulas dos objectos das bridges
            MethodInvocation mi = new MethodInvocation();
            mi.setInvocation(new VarLiteral("callObjBridgeCalculate"));
            code.add(new ExpressionStatement(mi));
            ArrayList paramsR = new ArrayList(1);
            paramsR.add(new VarLiteral("from"));
            EList t = new EList();
            t.setList(paramsR);
            mi.setParameters(t);
        
            //if(from != null){
            IfStatement ifexp = new IfStatement();
            BinaryExpression beIfexp = new BinaryExpression();
            beIfexp.setLeftExpression(new VarLiteral("from"));
            beIfexp.setOperator(JavaTokenTypes.NOT_EQUAL);
            beIfexp.setRightExpression(new VarLiteral("null"));
            ifexp.setCondition(beIfexp);
            
            IfStatement ifexpcmp;
            BinaryExpression beIfexpCmp;
            MethodInvocation mtcmp;
            ArrayList r;
            EList el = null;
            ArrayList codeThen = new ArrayList();
            BlockStatement auxBl;
            BlockStatement blThen;

            for (int i = 0; i < refreshAtt.size(); i++)
            {
//                if (!((FormuleHeight) objs[i]).defaulCode())
//                {
//                    if (((FormuleHeight) objs[i]).getBridgeName() == null)
//                    {
                        auxBl = getFormuleLine(
                                    (String)refreshAtt.get(i));
                        if(auxBl != null)
                        {
                            ifexpcmp = new IfStatement();
                            beIfexpCmp = new BinaryExpression();
                            beIfexpCmp.setLeftExpression(new VarLiteral("from"));
                            beIfexpCmp.setOperator(JavaTokenTypes.DOT);
                            beIfexpCmp.setRightExpression(new VarLiteral("equals"));
                            mtcmp = new MethodInvocation();
                            mtcmp.setInvocation(beIfexpCmp);
                            r = new ArrayList(1);
                            r.add(new StringLiteral((String) refreshAtt.get(i)));
                            el = new EList();
                            el.setList(r);
                            mtcmp.setParameters(el);
                            ifexpcmp.setCondition(mtcmp);
                            ifexpcmp.setThenStatement(auxBl);
                            codeThen.add(ifexpcmp);
//                        }
//                    }
                    /*
                    else
                    {
                        codeThen.add(new ExpressionStatement(getBridgeFormule(
                                    ((FormuleHeight) objs[i]).getBridgeName(),
                                    ((FormuleHeight) objs[i]).getAttributeName())));
                    }
                    */
                }                
            }
            blThen = new BlockStatement(codeThen);
            ifexp.setThenStatement(blThen);

            
            ArrayList codeElse = new ArrayList();

            ArrayList whileCode = new ArrayList();
            VarDeclarationStatement end = new VarDeclarationStatement();
            end.setType(new TypeClause(PrimitiveType.BOOLEAN));
            BooleanLiteral bool = new BooleanLiteral();
            bool.setValue(false);
            AssignExpression assignBool = new AssignExpression();
            assignBool.setExpression(bool);
            end.setInit(assignBool);
            end.setName("end");
            whileCode.add(end);
            UnaryExpression not = new UnaryExpression();
            not.setOperator(JavaTokenTypes.LNOT);
            not.setExpression(new VarLiteral("end"));

            for (int i = 0; i < objs.length; i++)
            {
                if (!((FormuleHeight) objs[i]).defaulCode())
                {
                    if (((FormuleHeight) objs[i]).getBridgeName() == null)
                    {
                        codeElse.add(new ExpressionStatement(getFormuleAllLines(
                                    ((FormuleHeight) objs[i]).getAttributeName())));
                    }
                    else
                    {
                        codeElse.add(new ExpressionStatement(getBridgeFormule(
                                    ((FormuleHeight) objs[i]).getBridgeName(),
                                    ((FormuleHeight) objs[i]).getAttributeName())));
                    }
                }
            }
            
            if(codeElse.size() > 0)
            {
                //if allcalculated
                MethodInvocation miAllcal = new MethodInvocation();
                miAllcal.setInvocation(new VarLiteral("allCalculated"));
                ArrayList parmm = new ArrayList(1);
                parmm.add(new VarLiteral("table"));
                EList elAllcal = new EList();
                elAllcal.setList(parmm);
                miAllcal.setParameters(elAllcal);
                
                IfStatement ifExp = new IfStatement();
                ifExp.setCondition(miAllcal);
                ArrayList parifBl = new ArrayList(1);
                BinaryExpression assignEnd = new BinaryExpression();
                assignEnd.setLeftExpression(new VarLiteral("end"));
                assignEnd.setOperator(JavaTokenTypes.ASSIGN);
                BooleanLiteral endAssTrue = new BooleanLiteral();
                endAssTrue.setValue(true);
                assignEnd.setRightExpression(endAssTrue);                
                parifBl.add(new ExpressionStatement(assignEnd));
                BlockStatement ifBl = new BlockStatement(parifBl); 
                ifExp.setThenStatement(ifBl);
                //codeElse.add(ifExp);
            
                
                BlockStatement whileBl = new BlockStatement(codeElse);
                WhileStatement whileStat = new WhileStatement(not, whileBl);
                whileCode.add(whileStat);
            
            
//                BlockStatement blElse = new BlockStatement(whileBl);
                BlockStatement blElse = new BlockStatement(codeElse);
                ifexp.setElseStatement(blElse);
            }
            code.add(ifexp);
            BlockStatement blAux = new BlockStatement(code);
            ifexpModeTempl.setThenStatement(blAux);
            ArrayList auxLL = new ArrayList(1);
            auxLL.add(ifexpModeTempl);
            
//            code.add(blElse);
            BlockStatement bl = new BlockStatement(auxLL);
            mdecl.setCode(bl);
        }

        return mdecl.toJAVA();
    }


    public String getDependences()
    {
        MethodDeclaration mdecl = new MethodDeclaration();
        ModifierClause mc = new ModifierClause();
        mc.setModifier(PrimitiveModifier.PUBLIC);
        mdecl.setModifiers(mc);
        mdecl.setName("getDependences");

        //mdecl.setThrow(new VarLiteral("boRuntimeException"));
        ParameterDef pdef = new ParameterDef();
        pdef.setName("attributeName");
        pdef.setType(new TypeClause("String"));

        Parameters p = new Parameters();
        ArrayList params = new ArrayList(1);
        params.add(pdef);
        p.setList(params);
        mdecl.setParameters(p);
        mdecl.setType(new TypeClause("String[]"));

        ArrayList ifexps = new ArrayList();

        BlockStatement bls = new BlockStatement();
        StatementSymbol auxStat;
        FormuleHeight fh = null;
        for (int i = 0; i < formules.size(); i++)
        {
            fh = (FormuleHeight)formules.get(i);
            auxStat= contructIfHeight(fh.getName());
            if(auxStat != null)
                ifexps.add(auxStat);
        }

        ReturnStatement r = new ReturnStatement();       
        r.setReturnExp(new VarLiteral("null"));
        ifexps.add(r);
        bls.setStatements(ifexps);
        mdecl.setCode(bls);

        return mdecl.toJAVA();
    }


    private MethodInvocation getBridgeFormule(String bridgeName, String attName)
    {
        MethodInvocation mi = new MethodInvocation();
        mi.setInvocation(new VarLiteral("calculateFormulaForBridge"));

        ArrayList aux = new ArrayList();
        aux.add(new StringLiteral(bridgeName));
        aux.add(new StringLiteral(attName));

        EList el = new EList();
        el.setList(aux);
        mi.setParameters(el);

        return mi;
    }

    private BlockStatement getFormuleLine(String node)
    {    
        FormuleHeight fh = getHeight(formules, node);
        ArrayList dp = whoDependsFromMe(node, formules);
        if(dp != null && dp.size() > 0)
        {
            Object[] objs = sort(dp);
            ArrayList code = new ArrayList();
            BinaryExpression bFormul, beFormul, b, be;
            MethodInvocation miFormul, mi;
            ArrayList aux, auxPr;
            EList el, elPr;
            for(int i = 0; i < objs.length; i++)
            {
                if(((FormuleHeight)objs[i]).getBridgeName() != null)
                {
                    code.add(new ExpressionStatement(getBridgeFormule(
                                    ((FormuleHeight) objs[i]).getBridgeName(),
                                    ((FormuleHeight) objs[i]).getAttributeName())));
                }
                else
                {
                    miFormul = new MethodInvocation();
                    miFormul.setInvocation(new VarLiteral("getDependences"));
                    auxPr = new ArrayList(1);
                    auxPr.add(new StringLiteral(((FormuleHeight)objs[i]).getAttributeName()));
                    elPr = new EList();
                    elPr.setList(auxPr);
                    miFormul.setParameters(elPr);
    
                    //this.assignedQueue.setValueObject(
                    b = new BinaryExpression();
                    b.setLeftExpression(new VarLiteral("this"));
                    b.setRightExpression(new VarLiteral(((FormuleHeight)objs[i]).getAttributeName()));
                    b.setOperator(JavaTokenTypes.DOT);
    
                    be = new BinaryExpression();
                    be.setLeftExpression(b);
                    be.setRightExpression(new VarLiteral("setValueFormula"));
                    be.setOperator(JavaTokenTypes.DOT);
    
                    mi = new MethodInvocation();
                    mi.setInvocation(be);
    
                    aux = new ArrayList();
                    aux.add(new VarLiteral("table"));
                    aux.add(miFormul);
    
                    el = new EList();
                    el.setList(aux);
                    mi.setParameters(el);
                    code.add(new ExpressionStatement(mi));
                }
            }
            
            if(code.size() > 0)
            {
                //if allcalculated
                MethodInvocation miAllcal = new MethodInvocation();
                miAllcal.setInvocation(new VarLiteral("allCalculated"));
                ArrayList parmm = new ArrayList(1);
                parmm.add(new VarLiteral("table"));
                EList elAllcal = new EList();
                elAllcal.setList(parmm);
                miAllcal.setParameters(elAllcal);
                
                IfStatement ifExp = new IfStatement();
                ifExp.setCondition(miAllcal);
                ArrayList parifBl = new ArrayList(1);
                BinaryExpression assignEnd = new BinaryExpression();
                assignEnd.setLeftExpression(new VarLiteral("end"));
                assignEnd.setOperator(JavaTokenTypes.ASSIGN);
                BooleanLiteral endAssTrue = new BooleanLiteral();
                endAssTrue.setValue(true);
                assignEnd.setRightExpression(endAssTrue);                
                parifBl.add(new ExpressionStatement(assignEnd));
                BlockStatement ifBl = new BlockStatement(parifBl); 
                ifExp.setThenStatement(ifBl);
                //code.add(ifExp);
                
                BlockStatement bl = new BlockStatement(code);
            
                ArrayList whileCode = new ArrayList();
                VarDeclarationStatement end = new VarDeclarationStatement();
                end.setType(new TypeClause(PrimitiveType.BOOLEAN));
                BooleanLiteral bool = new BooleanLiteral();
                bool.setValue(false);
                AssignExpression assignBool = new AssignExpression();
                assignBool.setExpression(bool);
                end.setInit(assignBool);
                end.setName("end");
                whileCode.add(end);
                UnaryExpression not = new UnaryExpression();
                not.setOperator(JavaTokenTypes.LNOT);
                not.setExpression(new VarLiteral("end"));
                WhileStatement whileStat = new WhileStatement(not, bl);
                whileCode.add(whileStat);
//                BlockStatement whileBl = new BlockStatement(whileCode);
                BlockStatement whileBl = new BlockStatement(code);
                return whileBl;
                
            }
            BlockStatement bl = new BlockStatement(code);
            return bl;
        }
        else return null;
    }

    private MethodInvocation getFormuleAllLines(String node)
    {
        //this.assignedQueue.formule()
/*        
        BinaryExpression bFormul = new BinaryExpression();
        bFormul.setLeftExpression(new VarLiteral("this"));
        bFormul.setRightExpression(new VarLiteral(node));
        bFormul.setOperator(JavaTokenTypes.DOT);

        BinaryExpression beFormul = new BinaryExpression();
        beFormul.setLeftExpression(bFormul);
        beFormul.setRightExpression(new VarLiteral("formula"));
        beFormul.setOperator(JavaTokenTypes.DOT);

        MethodInvocation miFormul = new MethodInvocation();
        miFormul.setInvocation(beFormul);
        */
        
        MethodInvocation miFormul = new MethodInvocation();
        miFormul.setInvocation(new VarLiteral("getDependences"));
        ArrayList auxPr = new ArrayList(1);
        auxPr.add(new StringLiteral(node));
        EList elPr = new EList();
        elPr.setList(auxPr);
        miFormul.setParameters(elPr);

        //this.assignedQueue.setValueObject(
        BinaryExpression b = new BinaryExpression();
        b.setLeftExpression(new VarLiteral("this"));
        b.setRightExpression(new VarLiteral(node));
        b.setOperator(JavaTokenTypes.DOT);

        BinaryExpression be = new BinaryExpression();
        be.setLeftExpression(b);
        be.setRightExpression(new VarLiteral("setValueFormula"));
        be.setOperator(JavaTokenTypes.DOT);

        MethodInvocation mi = new MethodInvocation();
        mi.setInvocation(be);

        ArrayList aux = new ArrayList();
        ConstructorExpression ce = new ConstructorExpression();
        VarLiteral v = new VarLiteral("Hashtable");
        ce.setExpression(v);
        aux.add(ce);
        aux.add(miFormul);

        EList el = new EList();
        el.setList(aux);
        mi.setParameters(el);

        return mi;
    }

    public StatementSymbol contructIf(String attName)
    {
        IfStatement ifexp = new IfStatement();

        BinaryExpression b = new BinaryExpression();
        b.setLeftExpression(new VarLiteral("attributeName"));
        b.setOperator(JavaTokenTypes.DOT);
        b.setRightExpression(new VarLiteral("equals"));

        MethodInvocation mtInv = new MethodInvocation();
        mtInv.setInvocation(b);
        mtInv.setReturnType("boolean");
        mtInv.setParameters(new StringLiteral(attName));

        ifexp.setCondition(mtInv);
        
        if(onChangeSubmitCode.get(attName) == null)
        {
            ReturnStatement r = new ReturnStatement();
            BooleanLiteral bl = new BooleanLiteral();
            bl.setValue(true);
            r.setReturnExp(bl);
            ifexp.setThenStatement(r);
        }
        else
        {
            ifexp.setThenStatement((StatementSymbol)onChangeSubmitCode.get(attName));        
        }

        return ifexp;
    }

    public StatementSymbol contructIfHeight(String attName)
    {
        IfStatement ifexp = new IfStatement();

        BinaryExpression b = new BinaryExpression();
        b.setLeftExpression(new VarLiteral("attributeName"));
        b.setOperator(JavaTokenTypes.DOT);
        b.setRightExpression(new VarLiteral("equals"));

        MethodInvocation mtInv = new MethodInvocation();
        mtInv.setInvocation(b);
        mtInv.setReturnType("boolean");
        mtInv.setParameters(new StringLiteral(attName));

        ifexp.setCondition(mtInv);
        
        ReturnStatement r = new ReturnStatement();
        ConstructorExpression ce = new ConstructorExpression();
        VarLiteral v = new VarLiteral("String");
        ce.setExpression(v);
        ArrayList p = new ArrayList();
        FormuleHeight fh = getHeight(formules, attName);
        if(fh != null)
        {
            ArrayList l = fh.dependof();
            FormuleHeight aux = null;
            for(int i = 0; l != null && i < l.size(); i++)
            {
                aux = getHeight(formules, (String)l.get(i));
                //if(aux != null && (bridgesNames.contains(aux.getName()) || !aux.defaulCode()))
                if(aux != null && !aux.defaulCode())
                {
                    p.add(new StringLiteral((String)l.get(i)));
                }
            }
        }

        if(p.size() == 0)
        {
            return null;   
        }
        else
        {
            EList el = new EList();
            el.setList(p);
            ce.setArrayInit(el);
            r.setReturnExp(ce);
        }        
        ifexp.setThenStatement(r);

        return ifexp;
    }


    public String getExpressionJava() 
    {
        if ((code_string != null) && !"".equals(code_string.trim()))
        {
            try
            {
                if(code.getLanguage()==boDefXeoCode.LANG_JAVA )
                {
                    String toRet = getCodeJava(code_string);
                    if(toRet.startsWith("{"))
                    {
                        if(toRet.indexOf(";") > 0)
                            return toRet.substring(toRet.indexOf("{") + 1, toRet.indexOf(";"));
                        else
                            return toRet.substring(toRet.indexOf("{") + 1, toRet.indexOf("}"));
                    }
                    else
                    {
                        if(toRet.indexOf(";") > 0)
                            return toRet.substring(0, toRet.indexOf(";"));
                        else
                            return toRet;
                    }
                }
                else
                {
                    StringBuffer toReturn = new StringBuffer();
                    XEORecognizer xeoRec = new XEORecognizer("{" + code_string + "}",
                            bodef, att, attributes, refreshAtt, node, bridge,
                            bridgeAttName, formatts);

                    BlockStatement bl = (BlockStatement)codeTreater(xeoRec);
                    if(bl.getStatements().size() > 0)
                    {
                        String s = ((Symbol)bl.getStatements().get(0)).toJAVA();                        
                        return s.substring(0, s.indexOf(";"));
                    }
                    return "";
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return "";
    }

    public String getExpressionStatementJava()
    {
        if ((code != null) && !"".equals(code_string.trim()))
        {
            try
            {
                if(code.getLanguage() == boDefXeoCode.LANG_JAVA )
                {
                    String toRet = getCodeJava(code_string);
                    if(toRet.startsWith("{"))
                    {
                        if(toRet.indexOf(";") > 0)
                            return toRet.substring(toRet.indexOf("{") + 1, toRet.indexOf(";") + 1);
                        else
                            return toRet.substring(toRet.indexOf("{") + 1, toRet.indexOf("}")) + ";";
                    }
                    else
                    {
                        if(toRet.indexOf(";") > 0)
                            return toRet.substring(0, toRet.indexOf(";") + 1);
                        else
                            return toRet;
                    }
                }
                else
                {
                    StringBuffer toReturn = new StringBuffer();
                    XEORecognizer xeoRec = new XEORecognizer("{" + code_string + "}",
                            bodef, att, attributes, refreshAtt, node, bridge,
                            bridgeAttName, formatts);

                    BlockStatement bl = (BlockStatement)codeTreater(xeoRec);
                    if(bl.getStatements().size() > 0)
                    {
                        return ((Symbol)bl.getStatements().get(0)).toJAVA();
                    }
                    return "";
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return "";
    }

    public String getStatementsJava()
    {
        if ((code_string != null) && !"".equals(code_string.trim()))
        {
            try
            {
                if(code.getLanguage() == boDefXeoCode.LANG_JAVA)
                {
                    String toRet = getCodeJava(code_string);
                    if(!toRet.startsWith("{"))
                    {
                        return "{" + toRet + "}"; 
                    }
                    return toRet;
                }
                else
                {
                    StringBuffer toReturn = new StringBuffer();
                    XEORecognizer xeoRec = new XEORecognizer("{" + code_string + "}",
                            bodef, att, attributes, refreshAtt, node, bridge,
                            bridgeAttName, formatts);

                    Symbol codeS = codeTreater(xeoRec);
                    return codeS.toJAVA();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return "";
    }


    public String getHaveDefault()
    {
        MethodDeclaration mdecl = new MethodDeclaration();
        ModifierClause mc = new ModifierClause();
        mc.setModifier(PrimitiveModifier.PUBLIC);
        mdecl.setModifiers(mc);
        mdecl.setName("haveDefaultValue");
        mdecl.setType(new TypeClause(PrimitiveType.BOOLEAN));
        mdecl.setCode(getHasDefaultCode(haveDefault));
        return mdecl.toJAVA();
    }

    public String getJava()
    {
        try
        {
        MethodDeclaration mdecl = new MethodDeclaration();
        ModifierClause mc = new ModifierClause();
        mc.setModifier(PrimitiveModifier.PUBLIC);
        mdecl.setModifiers(mc);
        mdecl.setName(node);
        mdecl.setThrow(new VarLiteral("boRuntimeException"));

        if ("validate".equals(node) || "required".equals(node) ||
                "disableWhen".equals(node) || "hiddenWhen".equals(node) ||
                "onChangeSubmit".equals(node) || "canChangeLov".equals(node))
        {
            mdecl.setType(new TypeClause(PrimitiveType.BOOLEAN));
        }
        else if ("defaultValue".equals(node) || "formula".equals(node))
        {
            mdecl.setType(new TypeClause("String"));
        }
//        else if ("formula".equals(node))
//        {
//            mdecl.setType(new TypeClause("String"));
//        }
        else //condition
        {
            mdecl.setType(new TypeClause("String[]"));
        }
        if("onChangeSubmit".equals(node))
        {
            if (!refreshAtt.contains(att.getName()))
            {
                refreshAtt.add(att.getName());
            }
        }

        //mdecl.setParameters();
        if ((code_string != null) && !"".equals(code_string.trim()))
        {
            try
            {
                //            if("default".equals(node))
                //                {
                //                    logger.finest("a");
                //                }
                //                logger.finest(" ---E code: " + code + " node: " + node);
                if(code.getLanguage()== boDefXeoCode.LANG_JAVA )
                {                    
                    if(codeJavaDepends != null)
                    {
                        XEORecognizer xeoRec = new XEORecognizer("{" + code_string + "}",
                            bodef, att, attributes, refreshAtt, node, bridge,
                            bridgeAttName, formatts);
                        int hei = 0;
                        for(int i = 0; i < codeJavaDepends.length; i++)
                        {
                            hei += xeoRec.getHeight(codeJavaDepends[i]);
                        }
                        if ("formula".equals(node))
                        {                                                                
                            if (!bridge)
                            {
                                insertIfNotExists(formules,
                                    new FormuleHeight(att.getName(),
                                    hei, new ArrayList(Arrays.asList(codeJavaDepends))));
                            }
                            else
                            {
                                insertIfNotExists(formules,
                                new FormuleHeight(bridgeAttName, att.getName(),
                                hei, new ArrayList(Arrays.asList(codeJavaDepends))));
                            }                            
                        }
                        //else if ("defaultValues".equals(node))
                        //{
                            for (int i = 0; i < codeJavaDepends.length; i++)
                            {
                                if (!refreshAtt.contains(codeJavaDepends[i]))
                                {
                                    refreshAtt.add(codeJavaDepends[i]);
                                }
                            }
                        //}
                    }
                    if(!"onChangeSubmit".equals(node))
                    {
                        mdecl.setCode(getCodeJava(code_string));
                        return mdecl.toJAVA();
                    }
                    else
                    {
                        BlockStatement block = new BlockStatement(getCodeJava(code_string));
                        onChangeSubmitCode.put(node, block);
                        return "";
                    }
                }
                else
                {
                    XEORecognizer xeoRec = new XEORecognizer("{" + code_string + "}",
                            bodef, att, attributes, refreshAtt, node, bridge,
                            bridgeAttName, formatts);
    
                    //try{}catch(Exception e){}
                    TryStatement ts = new TryStatement();
                    
                    Symbol codeS = codeTreater(xeoRec);
                    ts.setBody((StatementSymbol) codeS);
    
                    CatchStatement cs = new CatchStatement();
                    ParameterDef pdef = new ParameterDef();
                    TypeClause tc = new TypeClause("Exception");
                    pdef.setType(tc);
                    pdef.setName("e");
                    cs.setParam(pdef);
                    //cs.setBody(setDefault(true));
    
                    ArrayList r = new ArrayList();
                    r.add(cs);
                    ts.setCatchClauses(r);
    
                    ArrayList code = new ArrayList();
                    code.add(ts);
                    
                    //return final
                    code.add(((BlockStatement)setDefault(true)).getStatements().get(0));
                    
                    if("onChangeSubmit".equals(node))
                    {
                        onChangeSubmitCode.put(att.getName(), new BlockStatement(code));
                    }
                    else
                    {
                        mdecl.setCode(new BlockStatement(code));
                    }
    
                    //                logger.finest(" ---O code: " + code + " node: " + node);
                    if ("formula".equals(node))
                    {
                        if (!bridge)
                        {
                            insertIfNotExists(formules,
                                new FormuleHeight(att.getName(),
                                    xeoRec.getHeight(), xeoRec.getDependence()));
                        }
                        else
                        {
                            insertIfNotExists(formules,
                                new FormuleHeight(bridgeAttName, att.getName(),
                                    xeoRec.getHeight(), xeoRec.getDependence()));
                        }
                    }
                    //else if ("defaultValues".equals(node))
                    //{
                        ArrayList h = xeoRec.getDependence();
                        if(h != null)
                        {
                            for (int i = 0; i < h.size(); i++)
                            {
                                if (!refreshAtt.contains((String) h.get(i)))
                                {
                                    refreshAtt.add(h.get(i));
                                }
                            }
                        }
                    //}
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            if("onChangeSubmit".equals(node))
            {
                onChangeSubmitCode.put(att.getName(), setDefault(false));
            }
            else
            {
                mdecl.setCode(setDefault(false));
            }
        }

        if("onChangeSubmit".equals(node))
        {
            return "";
        }
         return mdecl.toJAVA();
        }
        catch( Exception e )
        {
            e=e;
            return null;
        }
    }

    private StatementSymbol getHasDefaultCode(boolean value)
    {
        return getDefault(value);
    }

    private StatementSymbol setDefault(boolean catchCode)
    {
        if ("validate".equals(node) || "canChangeLov".equals(node))
        {
            return getDefault(true);
        }
        else if ("required".equals(node) || "disableWhen".equals(node) ||
                "hiddenWhen".equals(node) || "onChangeSubmit".equals(node))
        {
            return getDefault(false);
        }
        else if ("formula".equals(node) && !catchCode)
        {
            if (!bridge)
            {
                insertIfNotExists(formules,
                    new FormuleHeight(null, att.getName(), 0, !catchCode, null));
            }
            else
            {
                insertIfNotExists(formules,
                    new FormuleHeight(bridgeAttName, att.getName(), 0,
                        !catchCode, null));
            }

            return getDefaultNull();
        }
        else
        {
            return getDefaultNull();
        }
    }

    public StatementSymbol getDefault(boolean v)
    {
        BlockStatement block = new BlockStatement();
        ArrayList r = new ArrayList();
        BooleanLiteral b = new BooleanLiteral();
        b.setValue(v);
        r.add(new ReturnStatement(b));
        block.setStatements(r);

        return block;
    }

    public StatementSymbol getDefaultNull()
    {
        BlockStatement block = new BlockStatement();
        ArrayList r = new ArrayList();
        r.add(new ReturnStatement(new VarLiteral("null")));
        block.setStatements(r);

        return block;
    }

    private static void insertIfNotExists(ArrayList arr, FormuleHeight elem)
    {
        boolean found = false;

        for (int i = 0; (i < arr.size()) && !found; i++)
        {
            if (elem.getAttributeName().equals(((FormuleHeight) arr.get(i)).getAttributeName()))
            {
                found = true;
            }
        }

        if (!found)
        {
            arr.add(elem);
        }
    }

    private FormuleHeight getHeight(ArrayList arr, String attName)
    {
        boolean found = false;

        for (int i = 0; (i < arr.size()) && !found; i++)
        {
            if (attName.equals(((FormuleHeight) arr.get(i)).getAttributeName()))
            {
                return ((FormuleHeight) arr.get(i));
            }
        }
        return null;
    }

    //SUM | SUBTRACT | 
    private static String resumeCommmands(String s, String attName,
        String boName, Hashtable formatts) throws boRuntimeException
    {
        String aux = resumeCommmands(s, "SUM", attName, boName, formatts);
        aux = resumeCommmands(aux, "ROUND", attName, boName, formatts);
        aux = resumeCommmands(aux, "TO_DATE", attName, boName, formatts);

        return resumeCommmands(aux, "SUBTRACT", attName, boName, formatts);
    }

    private static String resumeCommmands(String s, String command,
        String attName, String boName, Hashtable formatts) throws boRuntimeException
    {
        try
        {
            if ((s != null) && (s.trim().length() > 0))
            {
                s = s.trim();

                int pos = 0;
                int start = -1;
                StringBuffer sb = new StringBuffer();
                String aux;
                int startPar = -1;
                int endPar = -1;
                int startLastParent = -1;
                int posParent = 0;
                int auxPos;
                int size = 0;
                String formatt = "yyyyMMdd";

                while ((start = s.indexOf(command, pos)) != -1)
                {
                    int va = start + command.length();

                    if (s.charAt(va) != '_')
                    {
                        sb = sb.delete(0, sb.length());
                        sb.append(s.substring(0, start));
                        startPar = s.indexOf("(", start) + 1;
                        endPar = getEndPar(s, startPar - 1);
                        aux = s.substring(startPar, endPar);

                        if ("ROUND".equals(command) || "TO_DATE".equals(command))
                        {
                            int virg;

                            if ((virg = getVirg(aux, aux.length() - 1)) != -1)
                            {
                                if("ROUND".equals(command))
                                {
                                    size = Integer.parseInt((aux.substring(virg +
                                                1)).trim());
                                }
                                else
                                {
                                    formatt = aux.substring(virg +1).trim();
                                }
                                aux = aux.substring(0, virg);
                            }
                        }

                        startLastParent = -1;
                        posParent = 0;
                        auxPos = 0;

                        while ((auxPos = aux.indexOf("parent", posParent)) != -1)
                        {
                            posParent += 6;
                            startLastParent = auxPos;
                        }

                        if (startLastParent == -1)
                        {
                            if ("ROUND".equals(command))
                            {
                                aux = command + "_" + size + "(" + aux + ")";
                            }
                            else if("TO_DATE".equals(command))
                            {
                                aux = command + "_" + verifyFormat(formatt, formatts) + "(" + aux + ")";
                            }
                            else
                            {
                                aux = command + "(" + aux + ")";
                            }
                        }
                        else
                        {
                            int auxPt = aux.indexOf(".", startLastParent);

                            if ("ROUND".equals(command))
                            {
                                aux = command + "_" + size + "(" + aux + ")";
                            }
                            else if("TO_DATE".equals(command))
                            {
                                aux = command + "_" + verifyFormat(formatt, formatts) + "(" + aux + ")";
                            }
                            else
                            {
                                aux = aux.substring(0, auxPt + 1) + command +
                                    "(" + aux.substring(auxPt + 1) + ")";
                            }
                        }

                        sb.append(aux);
                        sb.append(s.substring(endPar + 1));
                        s = sb.toString();
                        pos = startPar;
                    }
                    else
                    {
                        pos = start + command.length();
                    }
                }
            }
        }
        catch (Exception e)
        {
            throw new boRuntimeException("XEORecognizer",MessageLocalizer.getMessage("ERROR_VERIFYING_THE_ATTRIBUTE")+
                ": " + attName + " "+MessageLocalizer.getMessage("FROM_OBJECT")+": " +
                boName, e);
        }

        return s;
    }

    private Object[] sort(ArrayList list)
    {
        //calculate with dependence        
        if (list.size() > 1)
        {
            String auxName;

            for (int i = 0; i < list.size(); i++)
            {
                auxName = ((FormuleHeight) list.get(i)).getName();

                for (int j = 0; j < list.size(); j++)
                {
                    if (j != i)
                    {
                        if (((FormuleHeight) list.get(j)).dependof(auxName))
                        {
                            ((FormuleHeight) list.get(j)).addHeight(((FormuleHeight) list.get(
                                    i)).getHeight());
                        }
                    }
                }
            }
        }

        //ordenar por prioridade o cálculo das fórmulas
        Object[] objs = list.toArray();
        Arrays.sort(objs, new FormuleHeightComparator());

        return objs;
    }

    private static int getEndPar(String s, int start)
    {
        int counter = 0;

        for (; start < s.length(); start++)
        {
            if (s.charAt(start) == '(')
            {
                counter++;
            }

            if (s.charAt(start) == ')')
            {
                counter--;
            }

            if (counter == 0)
            {
                return start;
            }
        }

        return -1;
    }

    private static int getVirg(String s, int endPar)
    {
        int counter = 0;

        for (; endPar > 0; endPar--)
        {
            if (s.charAt(endPar) == ',')
            {
                return endPar;
            }
        }

        return -1;
    }

    private static String getCodeJava(String code)
    {
//        int startPar = code.indexOf("(");
//        int endPar = getEndPar(code, startPar);
//        return code.substring(startPar + 1, endPar);
        return code;
    }

    private Symbol codeTreater(XEORecognizer xeo) throws boRuntimeException
    {
        BlockStatement stat = (BlockStatement) xeo.getStatement();
//        logger.finest(stat.toJAVA());

        Hashtable resumeCode = xeo.getResumeCode();
        ArrayList codeList = new ArrayList();
        BlockStatement toRet = new BlockStatement();
        ArrayList toRetStats = new ArrayList();

        if (resumeCode.size() > 0)
        {
            Enumeration e = resumeCode.elements();
            BlockStatement block;
            ArrayList aux;

            while (e.hasMoreElements())
            {
                block = (BlockStatement) e.nextElement();
                aux = block.getStatements();

                for (int i = 0; i < aux.size(); i++)
                {
                    toRetStats.add(aux.get(i));
                }
            }

            //            toRetStats.add(stat);
        }

        toRetStats.addAll(stat.getStatements());
        toRet.setStatements(toRetStats);

        return toRet;
    }

    /**
     *
     * @param args
     * @since
     */
/*
        public static void main(String[] args)
        {
            try
            {
                logger.finest(resumeCommmands("TO_DATE(parent_asad.parent_dd.dao.aaa, 'yyy-mi-dd  MM at SDSD');", "TO_DATE", "aaa", "ddd"));
            }
            catch (boRuntimeException e)
            {
                e.printStackTrace();
                System.exit(0);
            }
        }
*/        
    class FormuleHeight
    {
        String attName = null;
        String bridgeName = null;
        int height;
        boolean defaultVal = false;
        ArrayList depend = null;

        public FormuleHeight(String bridgeName, String attName, int height,
            ArrayList listOfDependence)
        {
            this.bridgeName = bridgeName;
            this.attName = attName;
            this.height = height;
            this.defaultVal = false;
            this.depend = listOfDependence;
        }

        public FormuleHeight(String bridgeName, String attName, int height,
            boolean defaultVal, ArrayList listOfDependence)
        {
            this.bridgeName = bridgeName;
            this.attName = attName;
            this.height = height;
            this.defaultVal = defaultVal;
            this.depend = listOfDependence;
        }

        public FormuleHeight(String attName, int height,
            ArrayList listOfDependence)
        {
            this.attName = attName;
            this.height = height;
            this.depend = listOfDependence;
        }

        public String getName()
        {
            return (bridgeName == null) ? attName : (bridgeName + "." +
            attName);
        }

        public int getHeight()
        {
            return height;
        }

        public String getAttributeName()
        {
            return attName;
        }

        public String getBridgeName()
        {
            return bridgeName;
        }

        public boolean defaulCode()
        {
            return defaultVal;
        }

        public boolean dependof(String s)
        {
            return (depend != null) ? depend.contains(s) : false;
        }
        
        public ArrayList dependof()
        {
            return depend;
        }

        public void addHeight(int h)
        {
            height += h;
        }
    }

    public class FormuleHeightComparator implements Comparator
    {
        private Long id;

        public FormuleHeightComparator()
        {
        }

        public int compare(Object o1, Object o2)
        {
            FormuleHeight fh1 = (FormuleHeight) o1;
            FormuleHeight fh2 = (FormuleHeight) o2;
            int h1 = fh1.getHeight();
            int h2 = fh2.getHeight();

            if (h1 == h2)
            {
                return 0;
            }

            if (h1 < h2)
            {
                return -1;
            }

            return 1;
        }
    }

    public static String verifyFormat(String formatt, Hashtable formatts)
    {
        int size = formatts.size() + 1;
        String fName = "formatts_" + size;
        formatt = formatt.replaceAll("\"", "");
        formatt = formatt.replaceAll("\'", "");
        formatts.put(fName, formatt);
        return fName;
    }
    
    private static ArrayList whoDependsFromMe(String name, ArrayList formules)
    {
        ArrayList aux;
        ArrayList toRet = new ArrayList();
        for(int i = 0; i < formules.size(); i++)
        {
            aux = ((FormuleHeight)formules.get(i)).dependof();
            if(aux != null && aux.contains(name))
            {
                toRet.add(formules.get(i));
            }            
        }
        return toRet;
    
    }
    
    private static ArrayList IDependsFrom(String name, ArrayList formules)
    {
        FormuleHeight aux;
        ArrayList toRet = new ArrayList();
        for(int i = 0; i < formules.size(); i++)
        {
            aux = (FormuleHeight)formules.get(i);
            if(aux != null && name.equals(aux.getName()))
            {
                return aux.dependof();
            }            
        }
        return null;    
    }
    
    public String getForwardMapCode(String fwdMethodName) throws boRuntimeException
    {
        StringBuffer sb = new StringBuffer();
        boDefForwardObject[] fwdObjs =  bodef.getForwardObjects();
        boDefForwardObject fwdObj;
        HashMap map = null;
        Iterator it;
        String key, value;
        boDefHandler toBoDef = null;
        boolean treatedFwdObjs = false;
        
        for (int i = 0; i < fwdObjs.length && !treatedFwdObjs; i++) 
        {
            fwdObj = fwdObjs[i];
            if(fwdMethodName.equals(fwdObj.toBoObject()))
            {
                treatedFwdObjs = true;                
                sb.append("EboContext ctx = this.getEboContext();\n");
                sb.append("boObject newObj = boObject.getBoManager().createObject(ctx, \"")
                  .append(fwdObjs[i].toBoObject()).append("\");\n");
                sb.append("newObj.getAttribute(\"SYS_FROMOBJ\").setObject(this);\n");
                sb.append(fwdObj.getBeforeMapMethodName()).append("(newObj.getBoui());\n");
                toBoDef = boDefHandler.getBoDefinition(fwdObj.toBoObject());
                if(fwdObj.getMaps() != null)
                {
                    map = fwdObj.getMaps();
                    it = map.keySet().iterator();
                    while(it.hasNext())
                    {
                        key = (String)it.next();
                        value = (String)map.get(key);
                        sb.append(TRY).append("\n");
                        if(Utils.isBridgeAttrFromObj(key, bodef))
                        {
                            sb.append("netgest.bo.parser.util.Utils.copyBridge(")
                                .append(Utils.contructAttrFromObj(key, bodef, "this"))
                                .append(", ").append(Utils.contructAttrFromObj(value, toBoDef, "newObj"))
                                .append(");\n");
                        }
                        else
                        {                        
                            sb.append("netgest.bo.parser.util.Utils.copyAttribute(")
                                .append(Utils.contructAttrFromObj(key, bodef, "this"))
                                .append(", ").append(Utils.contructAttrFromObj(value, toBoDef, "newObj"))
                                .append(");\n");
                                
                        }
                        sb.append(CATCH).append("\n");
                    }
                }
                sb.append(fwdObj.getAfterMapMethodName()).append("(newObj.getBoui());\n");
            }
        }
        if(!treatedFwdObjs)
        {
            sb.append("return null;");
        }
        else
        {
            sb.append("return newObj;");
        }
        return sb.toString();
    }
    
}
