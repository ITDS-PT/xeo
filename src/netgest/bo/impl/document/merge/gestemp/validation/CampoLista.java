package netgest.bo.impl.document.merge.gestemp.validation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import netgest.bo.data.DataResultSet;
import netgest.bo.impl.document.merge.gestemp.GtCampo;
import netgest.bo.impl.document.merge.gestemp.GtCampoNFormula;
import netgest.bo.impl.document.merge.gestemp.GtCampoNJava;
import netgest.bo.impl.document.merge.gestemp.GtCampoNObjecto;
import netgest.bo.impl.document.merge.gestemp.GtValue;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;


public class CampoLista extends Campo {
    GtCampoNFormula campoFormula = null;
    GtCampoNJava campoJava = null;
    GtCampoNObjecto campoObjecto = null;
    int pos = -1;

    public CampoLista(EboContext boctx, GtCampoNFormula campo) {
        super(boctx, campo);
        this.campoFormula = campo;
    }

    public CampoLista(EboContext boctx, GtCampoNObjecto campo) {
        super(boctx, campo);
        this.campoObjecto = campo;
    }

    public CampoLista(EboContext boctx, GtCampoNJava campo) {
        super(boctx, campo);
        this.campoJava = campo;
    }

    public String getNome() {
        if (campoFormula != null) {
            return campoFormula.getNome();
        }

        if (campoObjecto != null) {
            return campoObjecto.getNome();
        }

        if (campoJava != null) {
            return campoJava.getNome();
        }

        return null;
    }

    public Object getValue() {
        ArrayList r = getValues();

        if ((r != null) && (r.size() > pos)) {
            return (Long) r.get(pos);
        }

        return null;
    }

    public ArrayList getValues() {
        if (campoFormula != null) {
            return getValores(campoFormula);
        }

        if (campoObjecto != null) {
            return getValores(campoObjecto);
        }

        if (campoJava != null) {
            return getValores(campoJava);
        }

        return null;
    }

    private ArrayList getValores(GtCampo campo) {
        return (campo.getValue() == null) ? null
                                          : (ArrayList) campo.getValue()
                                                             .getValue();
    }

    public void novaLinha() throws boRuntimeException
    {
        pos++;
        GtCampo[] aAllCampos = getAllCampos();
        for (int i = 0; i < aAllCampos.length; i++) 
        {
            setCampo( aAllCampos[i].getNome(), null );
        }
        
    }

    public void setValor(String valor) throws boRuntimeException {
        if (campoFormula != null) {
            setValue(campoFormula, new Long(valor));
        }

        if (campoObjecto != null) {
            setValue(campoObjecto, new Long(valor));
        }

        if (campoJava != null) {
            setValue(campoJava, new Long(valor));
        }
    }

    public void setValor(long valor) throws boRuntimeException {
        if (campoFormula != null) {
            setValue(campoFormula, new Long(valor));
        }

        if (campoObjecto != null) {
            setValue(campoObjecto, new Long(valor));
        }

        if (campoJava != null) {
            setValue(campoJava, new Long(valor));
        }
    }

    private void setValue(GtCampo campo, Long valor) throws boRuntimeException {
        GtValue val = campo.getValue();

        if (val != null) {
            val = new GtValue();
        }

        ArrayList r = val.getValues();

        if (r == null) {
            r = new ArrayList();
        }

        r.add(pos, valor);
        val.addValues(r);
        campo.setValue(val);
    }

    public void setCampo(int pos, Object value) throws boRuntimeException {
        Campo c = getCampo(pos);

        if (c != null) {
            ArrayList r = c.getValores();

            if (r == null) {
                r = new ArrayList();
            }
            if( pos >= r.size() )
            {
                r.add( value );
            }
            else
            {
                r.set( pos, value );
            }
            c.setValores(r);
        }
    }

    public void setCampo(String nome, Object value) throws boRuntimeException {
        Campo c = getCampo(nome);

        if (c != null) {
            ArrayList r = c.getValores();

            if (r == null) {
                r = new ArrayList();
            }
            if( pos >= r.size() )
            {
                r.add( value );
            }
            else
            {
                r.set( pos, value );
            }
            c.setValores(r);
        }
    }

    public Campo getCampo(int pos) {
        GtCampo[] allCampos = getAllCampos();

        if ((allCampos != null) && (allCampos.length > pos)) {
            return new Campo(boctx, allCampos[pos]);
        }

        return null;
    }

    public Campo getCampo(String nome) {
        GtCampo c = null;
        GtCampo[] allCampos = getAllCampos();

        if (allCampos != null) {
            for (int i = 0; i < allCampos.length; i++) {
                c = allCampos[i];

                if (c.getNome().equals(nome)) {
                    return new Campo(boctx, c);
                }
            }
        }

        return null;
    }

    private GtCampo[] getAllCampos() {
        if (campoFormula != null) {
            return campoFormula.getAllCampos();
        }

        if (campoObjecto != null) {
            return campoObjecto.getAllCampos();
        }

        if (campoJava != null) {
            return campoJava.getAllCampos();
        }

        return null;
    }

    public boObject[] getValoresXEOObject() throws boRuntimeException {
        ArrayList r = getValues();

        if (r != null) {
            boObject[] toRet = new boObject[r.size()];

            for (int i = 0; i < r.size(); i++) {
                toRet[i] = boObject.getBoManager().loadObject(boctx,
                        ((Long) r.get(i)).longValue());
            }

            return toRet;
        }

        return null;
    }

    public boObject getValorXEOObject() throws boRuntimeException {
        Long boui = (Long) getValue();

        if (boui != null) {
            return boObject.getBoManager().loadObject(boctx, boui.longValue());
        }

        return null;
    }


    public void executeQuery( Contexto contexto, String sql ) throws boRuntimeException
    {
        executeQuery( contexto, sql, null );
    }

    public void executeQuery( Contexto contexto, String sql, Object arg1 ) throws boRuntimeException
    {
        executeQuery( contexto, sql, new Object[] { arg1 } );
    }

    public void executeQuery( Contexto contexto, String sql, Object arg1, Object arg2 ) throws boRuntimeException
    {
        executeQuery( contexto, sql, new Object[] { arg1, arg2 } );
    }

    public void executeQuery( Contexto contexto, String sql, Object arg1, Object arg2, Object arg3 ) throws boRuntimeException
    {
        executeQuery( contexto, sql, new Object[] { arg1, arg2, arg3 } );
    }

    public void executeQuery( Contexto contexto, String sql, Object arg1, Object arg2, Object arg3, Object arg4 ) throws boRuntimeException
    {
        executeQuery( contexto, sql, new Object[] { arg1, arg2, arg3, arg4 } );
    }

    
    public void executeQuery( Contexto contexto, String sql, Object[] arguments ) throws boRuntimeException
    {
        Connection con = contexto.getBDLigacao();
        
        PreparedStatement pstm = null;
        ResultSet         rslt = null;
        try 
        {
            GtCampo[] aAllCampos = getAllCampos();
            pstm = con.prepareStatement( sql );
            
            if( arguments != null )
            {
                for (int i = 0; i < arguments.length; i++) 
                {
                    if ( arguments[i] != null )
                    {
                        pstm.setObject( i + 1, arguments[i] );
                    }
                    else
                    {
                        pstm.setString( i + 1, null );
                    }
                }
                
            }
            
            rslt = pstm.executeQuery(  );
            
            while( rslt.next() )
            {
                novaLinha();
                for (int i = 0; i < aAllCampos.length; i++) 
                {
                    if ( rslt.findColumn( aAllCampos[ i ].getNome()) > 0  )
                    {
                        setCampo( aAllCampos[ i ].getNome(), rslt.getString( aAllCampos[ i ].getNome() ) );
                    }
                }
            }
        }
        catch (SQLException e) 
        {
            throw new RuntimeException( e );
        } 
        finally 
        {
            try
            {
                if( rslt != null ) rslt.close();
                if( pstm != null ) pstm.close();
            }
            catch (Exception e)
            {
                
            }
        }
    }
}
