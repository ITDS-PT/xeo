package netgest.bo.impl.document.merge.gestemp.validation;

import netgest.bo.impl.document.merge.gestemp.GtCampo;
import netgest.bo.impl.document.merge.gestemp.GtQuery;
import netgest.bo.impl.document.merge.gestemp.GtTemplate;
import netgest.bo.runtime.EboContext;


public class Modelo {
    private GtTemplate template = null;
    private GtQuery query = null;
    private EboContext boctx = null;

    public Modelo(EboContext boctx, GtTemplate template) {
        this.template = template;
        this.boctx = boctx;
    }

    //Queries    
    public int numeroQueries() {
        return (template.getQueries() == null) ? 0 : template.getQueries().length;
    }

    public Query getQuery(int pos) {
        return new Query(boctx, template.getQueries()[pos]);
    }

    public Query getQuery(String nome) {
        GtQuery q = null;

        if (template.getQueries() != null) {
            for (int i = 0; i < template.getQueries().length; i++) {
                q = template.getQueries()[i];

                if (q.getNome().equals(nome)) {
                    return new Query(boctx, q);
                }
            }
        }

        return null;
    }

    //Campos Manuais
    public int numeroCamposManuais() {
        return (template.getQueries() == null) ? 0 : template.getQueries().length;
    }

    public Campo getCampoManual(int pos) {
        return new Campo(boctx, template.getCamposManuais()[pos]);
    }

    public Campo getCampoManual(String nome) {
        GtCampo c = null;

        if (template.getCamposManuais() != null) {
            for (int i = 0; i < template.getCamposManuais().length; i++) {
                c = template.getCamposManuais()[i];

                if (c.getNome().equals(nome)) {
                    return new Campo(boctx, c);
                }
            }
        }

        return null;
    }
    
    public boolean registada()
    {
        return template.isRegistada();
    }
    
    public boolean avisoRecepcao()
    {
        return template.isAviso();
    }
    
    public boolean simples()
    {
        return template.isSimples();
    }
    
    public boolean carta()
    {
        return template.getChannel() == template.TYPE_CARTA;
    }
    
    public boolean fax()
    {
        return template.getChannel() == template.TYPE_FAX;
    }
    
    public boolean email()
    {
        return template.getChannel() == template.TYPE_EMAIL;
    }
    
    public boolean sms()
    {
        return template.getChannel() == template.TYPE_SMS;
    }
    
    
}
