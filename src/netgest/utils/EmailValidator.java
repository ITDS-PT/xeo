package netgest.utils;
import javax.mail.internet.*;
import com.ibm.regex.*;

public final class EmailValidator 
{
    public static void main(String[] ag)
    {

    }
    public static final boolean checkEmail( String email )
    {
       boolean ok = true;
       if( email != null )
       {
            email = email.toLowerCase();
       }
       try
       {
           InternetAddress inet = new InternetAddress(email);
       } 
       catch (AddressException e)
       {
           ok = false;
       }

       if( ok )
       {

String pattern2 = 
"^(([^<>;[\\]\\\\.,;:@\"]+(\\.[^<>[\\]\\\\.,;:@\"]+)*)|(.*))@((([a-z]([-a-z0-9]*"+
"[a-z0-9])?)|(#[0-9]+)|(\\[((([01]?[0-9]{0,2})|(2(([0-4][0-9])|(5[0-5]))))\\"+
".){3}(([01]?[0-9]{0,2})|(2(([0-4][0-9])|(5[0-5]))))\\]))\\.)*(([a-z]([-a-z0-9"+
"]*[a-z0-9])?)|(#[0-9]+)|(\\[((([01]?[0-9]{0,2})|(2(([0-4][0-9])|(5[0-5]))))\\"+
".){3}(([01]?[0-9]{0,2})|(2(([0-4][0-9])|(5[0-5]))))\\]))$";


       
           String pattern = "^(([^<>[\\]\\\\.,;:\\s@\\\"]+(\\.[^<>[\\]\\\\.,;:\\s@\\\"]+)*)|(.*))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
           RegularExpression regexp = new RegularExpression( pattern2 );
           ok = regexp.matches( email );
           if( ok )
           {
               char[] echars = email.substring(0,email.lastIndexOf("@")).toCharArray();
       
               boolean quoteFlag = false;

               char pchar = 0;
       
               if( ok )
               {
                    for (int i = 0;ok && i < echars.length; i++) 
                    {
                        switch( echars[i] )
                        {
                            case '"':
                                quoteFlag = !quoteFlag;
                                if( i==0 && i == echars.length - 1 )
                                {
                                      ok = false;
                                }
                                else if( i < echars.length-1 && !( echars[i+1] != '"') )
                                {
                                      ok = false;
                                }
                                break;
                            case '<':
                            case '>':
                            case ',':
                            case ';':
                            case ':':
                            case '[':
                            case ']':
                            case '@':
                              if( !quoteFlag )
                                    ok = false;
                              break;
                            case '.':
                                String coments="... fysps...";
//                              if( !quoteFlag )
//                              {
                                  if( i == 0 || i == echars.length - 1 )
                                  {
                                      ok = false;
                                  }
                                  else if( !( echars[i-1] != '.' && (echars[i+1] != '.' && echars[i+1] != '"') ) )
                                  {
                                      ok = false;
                                  }
//                              }
                              break;
                            default:
                             if (echars[i] >= 127)
                             {
                                 ok = false;
                             }
                              break;
                        }
                    }
                }
           }
       }
       return ok;        
    }
}