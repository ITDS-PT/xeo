/*Enconding=UTF-8*/
package netgest.bo.events;

import netgest.bo.runtime.*;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.ObjAttHandler;
import netgest.bo.runtime.boConvertUtils;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;

import java.math.BigDecimal;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class boFactura
{
    /**
     *
     * @since
     */
    public boFactura()
    {
    }

    public static void adicionar(boObject obj)
    {
        try
        {
            ObjAttHandler produto = (ObjAttHandler) obj.getAttribute("product");
            AttributeHandler quant = obj.getAttribute("prod_quantaty");
            AttributeHandler prodPrUnit = obj.getAttribute("prod_unityPrice");
            AttributeHandler prodIva = obj.getAttribute("prod_iva");

            if ((produto != null) && (produto.getObject() != null))
            {
                if ((quant != null) && (quant.getValueObject() != null))
                {
                    if ((prodPrUnit != null) &&
                            (prodPrUnit.getValueObject() != null))
                    {
                        if ((prodIva != null) &&
                                (prodIva.getValueObject() != null))
                        {
                            bridgeHandler bh = obj.getBridge("detail");

                            if (verifyAlreadyExists(bh,
                                        produto.getObject().getBoui()))
                            {
                                add(bh, produto.getObject().getBoui(),
                                    (quant.getValueObject() == null) ? 0
                                                                     : quant.getValueLong());
                            }
                            else
                            { //ainda não existe

                                boObject prod = boObject.getBoManager()
                                                        .createObject(obj.getEboContext(),
                                        "Factura_Detail");

                                //                                boObject prod = bh.addNewObject();
                                prod.getAttribute("PARENT").setValueLong(obj.getBoui());
                                prod.getAttribute("product").setValueLong(produto.getObject()
                                                                                 .getBoui());
                                prod.getAttribute("quantaty").setValueObject((quant.getValueObject() == null)
                                    ? BigDecimal.valueOf(0)
                                    : quant.getValueObject());
                                prod.getAttribute("description").setValueObject(obj.getAttribute(
                                        "prod_description").getValueObject());
                                prod.getAttribute("discount").setValueObject((obj.getAttribute(
                                        "prod_discount").getValueObject() == null)
                                    ? BigDecimal.valueOf(0)
                                    : obj.getAttribute("prod_discount")
                                         .getValueObject());
                                prod.getAttribute("unityPrice").setValueObject((prodPrUnit.getValueObject() == null)
                                    ? BigDecimal.valueOf(0)
                                    : prodPrUnit.getValueObject());
                                prod.getAttribute("iva").setValueObject((prodIva.getValueObject() == null)
                                    ? BigDecimal.valueOf(0)
                                    : prodIva.getValueObject());
                                bh.add(prod.getBoui());

                                //actualizar os campos com formula
                                //prod.calculateFormula();
                            }

                            //actualizar os campos com formula
                            //obj.calculateFormula(null);
                            //limpar os campos
                            obj.getAttribute("product").setValueObject(boConvertUtils.convertToBigDecimal(
                                    obj.getAttribute("product").defaultValue(),
                                    obj.getAttribute("product")));
                            obj.getAttribute("prod_quantaty").setValueObject(boConvertUtils.convertToBigDecimal(
                                    obj.getAttribute("prod_quantaty")
                                       .defaultValue(),
                                    obj.getAttribute("prod_quantaty")));
                            obj.getAttribute("prod_unityPrice").setValueObject(boConvertUtils.convertToBigDecimal(
                                    obj.getAttribute("prod_unityPrice")
                                       .defaultValue(),
                                    obj.getAttribute("prod_unityPrice")));
                            obj.getAttribute("prod_discount").setValueObject(boConvertUtils.convertToBigDecimal(
                                    obj.getAttribute("prod_discount")
                                       .defaultValue(),
                                    obj.getAttribute("prod_discount")));
                            obj.getAttribute("prod_iva").setValueObject(boConvertUtils.convertToBigDecimal(
                                    obj.getAttribute("prod_iva").defaultValue(),
                                    obj.getAttribute("prod_iva")));
                            obj.getAttribute("prod_description").setValueObject(obj.getAttribute(
                                    "prod_description").defaultValue());
                        }
                        else
                        {
                            obj.addErrorMessage("Preencha o valor do IVA.");
                        }
                    }
                    else
                    {
                        obj.addErrorMessage("Preencha o preço unitário.");
                    }
                }
                else
                {
                    obj.addErrorMessage("Preencha a quantidade.");
                }
            }
            else
            {
                obj.addErrorMessage("Seleccione o produto pretendido.");
            }
        }
        catch (boRuntimeException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static boolean verifyAlreadyExists(bridgeHandler bh, long boui)
        throws boRuntimeException
    {
        boObject prod;
        bh.beforeFirst();

        while (bh.next())
        {
            prod = bh.getObject().getAttribute("product").getObject();

            if (prod.getBoui() == boui)
            {
                return true;
            }
        }

        return false;
    }

    private static boolean add(bridgeHandler bh, long boui, long qt)
        throws boRuntimeException
    {
        boObject prod;
        boObject bucket;
        bh.beforeFirst();

        while (bh.next())
        {
            bucket = bh.getObject();
            prod = bucket.getAttribute("product").getObject();

            if (prod.getBoui() == boui)
            {
                bucket.getAttribute("quantaty").setValueLong(bucket.getAttribute(
                        "quantaty").getValueLong() + qt);
            }
        }

        return false;
    }

    public static void copyFromOrcamento(AttributeHandler attr)
    {
        boObject obj = attr.getParent();

        try
        {
            EboContext ctx = obj.getEboContext();

            if (obj != null)
            {
                if (obj.getAttribute("orcamento").getValueObject() != null)
                {
                    boObject orcamento = boObject.getBoManager().loadObject(ctx,
                            obj.getAttribute("orcamento").getValueLong());

                    if (orcamento != null)
                    {
                        //copia dos valores
                        obj.getAttribute("client").setValueObject(orcamento.getAttribute(
                                "client").getValueObject());
                        obj.getAttribute("name").setValueObject(orcamento.getAttribute(
                                "name").getValueObject());
                        obj.getAttribute("street").setValueObject(orcamento.getAttribute(
                                "street").getValueObject());
                        obj.getAttribute("global_disc").setValueObject(orcamento.getAttribute(
                                "global_disc").getValueObject());
                        obj.getAttribute("local").setValueObject(orcamento.getAttribute(
                                "local").getValueObject());
                        obj.getAttribute("cpostal").setValueObject(orcamento.getAttribute(
                                "cpostal").getValueObject());
                        obj.getAttribute("localcpostal").setValueObject(orcamento.getAttribute(
                                "localcpostal").getValueObject());
                        obj.getAttribute("contribuinte").setValueObject(orcamento.getAttribute(
                                "contribuinte").getValueObject());

                        bridgeHandler objDetail = obj.getBridge("detail");

                        if (!objDetail.isEmpty())
                        {
                            objDetail.beforeFirst();

                            while (objDetail.next())
                            {
                                objDetail.remove();
                            }
                        }

                        bridgeHandler orcDetail = orcamento.getBridge("detail");

                        if (!orcDetail.isEmpty())
                        {
                            orcDetail.beforeFirst();

                            boObject auxObj;

                            while (orcDetail.next())
                            {
                                auxObj = orcDetail.getObject().cloneObject();
                                objDetail.add(auxObj.getBoui());
                            }
                        }
                    }
                }
            }
        }
        catch (boRuntimeException ex)
        {
            ex.printStackTrace();
        }
    }
    
    public static  void onSaveFwdObjectToFactura(boObject encomenda, boObject fwdObject){
        bridgeHandler fwdObjBh = fwdObject.getBridge("detail");
        bridgeHandler bh = encomenda.getBridge("detail");
        boBridgeIterator fwdIt = fwdObjBh.iterator();
        boBridgeIterator it = bh.iterator();
        bridgeHandler hist = encomenda.getBridge("enc_factura");
        boBridgeIterator histIt = hist.iterator();
        boObject histObj;
        
        try
        {
            fwdIt.beforeFirst();
            long fwdBoui, nObjs;
            boObject fwdProd, prod;
            while(fwdIt.next())
            {
                fwdProd = fwdIt.currentRow().getObject();
                fwdBoui = fwdProd.getAttribute("product").getObject().getBoui();
                it.beforeFirst();
                while(it.next())
                {
                    prod = it.currentRow().getObject(); 
                    if(prod.getAttribute("product").getValueLong() == fwdBoui)
                    {
                        nObjs = prod.getAttribute("quantaty").getValueLong();
                        if(nObjs > 0)
                        {
                            histIt.beforeFirst();
                            boolean existsHist = false;
                            while(histIt.next())
                            {
                                histObj  = histIt.currentRow().getObject();
                                if(histObj.getAttribute("factura").getValueLong() == fwdObject.getBoui())
                                {
                                    existsHist = true;
                                    long objFact = histObj.getAttribute("quantaty").getValueLong();
                                    long factQt = fwdProd.getAttribute("quantaty").getValueLong();
                                    long prodFault = prod.getAttribute("quantaty").getValueLong();
                                    prod.getAttribute("quantaty").setValueLong(prodFault);
                                    long diff = objFact + (factQt - objFact);
                                    long calc = prodFault - (factQt - objFact);                                
                                    prod.getAttribute("quantaty").setValueLong(calc < 0 ? 0:calc);
                                    histObj.getAttribute("quantaty").setValueLong(diff > (objFact + prodFault) ? objFact + prodFault:diff);
                                }
                            }
                            if(!existsHist)
                            {
                                boObject newHist = hist.addNewObject();
                                newHist.getAttribute("factura").setObject(fwdObject);
                                long factQt = fwdProd.getAttribute("quantaty").getValueLong();
                                long prodFault = prod.getAttribute("quantaty").getValueLong();
                                long calc = prodFault - factQt;
                                prod.getAttribute("quantaty").setValueLong(calc < 0 ? 0:calc);
                                newHist.getAttribute("quantaty").setValueLong(factQt > prodFault ? prodFault:factQt);
                            }                        
                        }
                    }
                }
            }
        }
        catch (boRuntimeException e)
        {
            
        }        
    }
}
