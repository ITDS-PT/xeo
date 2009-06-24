/*Enconding=UTF-8*/

// Copyright (c) 2001 IIES
package netgest.bo.parser.modifiers;

/**
 * A Class class.
 * <P>
 * @author Francisco Câmara
 */
public class PrimitiveModifier extends Modifier {
    private PrimitiveModifier(String typeName)
    {
        super(typeName);
    }

  /**
    * Constante para o tipo primitivo VOID
    */
    public static final PrimitiveModifier PUBLIC = new PrimitiveModifier("public");

  /**
    * Constante para o tipo primitivo VOID
    */
    public static final PrimitiveModifier STATIC = new PrimitiveModifier("static");

  /**
    * Constante para o tipo primitivo VOID
    */
    public static final PrimitiveModifier PRIVATE = new PrimitiveModifier("private");

  /**
    * Constante para o tipo primitivo VOID
    */
    public static final PrimitiveModifier FINAL = new PrimitiveModifier("final");

  /**
    * Constante para o tipo primitivo VOID
    */
    public static final PrimitiveModifier VOLATILE = new PrimitiveModifier("volatile");

  /**
    * Constante para o tipo primitivo VOID
    */
    public static final PrimitiveModifier TRANSIENT = new PrimitiveModifier("transient");

  /**
    * Constante para o tipo primitivo VOID
    */
    public static final PrimitiveModifier SYNCHRONIZED  = new PrimitiveModifier("synchronized");

  /**
    * Constante para o tipo primitivo VOID
    */
    public static final PrimitiveModifier STRICT = new PrimitiveModifier("strict");

/**
    * Constante para o tipo primitivo VOID
    */
    public static final PrimitiveModifier INTERFACE = new PrimitiveModifier("interface");

  /**
    * Constante para o tipo primitivo VOID
    */
    public static final PrimitiveModifier PROTECTED = new PrimitiveModifier("protected");

  /**
    * Constante para o tipo primitivo VOID
    */
    public static final PrimitiveModifier NATIVE = new PrimitiveModifier("native");

  /**
    * Constante para o tipo primitivo VOID
    */
    public static final PrimitiveModifier ABSTRACT = new PrimitiveModifier("abstract");
}

 