package netgest.bo.localized;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.Vector;
import javax.swing.JOptionPane;
import netgest.bo.boConfig;
import netgest.utils.StringUtils;

public class GenerateLanguageFiles extends Thread
{
    public GenerateLanguageFiles()
    {
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            checkEncoding();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void checkEncoding(  ) throws Exception
    {
        //File file = new File("C:\\projects\\xeo\\v2.1\\src\\");

        File file = new File("C:\\projects\\xeo\\v2.1\\public_html\\attachfile.jsp");
        showAndCheckFile( file );
        //checkEncodingFile( file );
        //checkStringsFile( file );
    }

    public static void checkEncodingFile( File file ) throws Exception
    {
        File[] dirFiles = file.listFiles();
        for (int i = 0;dirFiles != null && i < dirFiles.length; i++)
        {
            if( dirFiles[i].isDirectory() )
            {
                checkEncodingFile( dirFiles[i] );
            }
            else
            {
                if( dirFiles[i].getName().endsWith("_pt_PT.properties") )
                {
                }
            }
        }
    }

    public static void showAndCheckFile( File file ) throws IOException
    {
        System.out.println( "----------------------------------------------------" );
        System.out.println( file.getAbsolutePath() );

        FileReader oFileReader = new FileReader( file );
        BufferedReader br = new BufferedReader( oFileReader );

        String line;
        line = br.readLine();
        while( line != null )
        {
            System.out.println( line );
            line = br.readLine();
        }

        br.close();
        oFileReader.close();

        if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(null, "Converte para o default?","" ,JOptionPane.OK_CANCEL_OPTION  ))
        {
            System.out.println("Converter:");

            FileInputStream oInput = new FileInputStream( file );
            InputStreamReader is = new InputStreamReader( oInput, "UTF-8" );

            File outFile = new File(file.getParent() + File.separator + file.getName() + "_2");

            FileWriter fw = new FileWriter( outFile );

            int iBr;
            char[] buffer = new char[8192];
            while( (iBr = is.read( buffer )) > 0 )
            {
                fw.write( buffer, 0, iBr );
            }
            fw.close();

            is.close();
            oInput.close();

            file.delete();

            outFile.renameTo( file );

        }

    }

    public static void checkStringsFile( File file ) throws Exception
    {
        File[] dirFiles = file.listFiles();
        for (int i = 0;dirFiles != null && i < dirFiles.length; i++)
        {
            if( dirFiles[i].isDirectory() )
            {
                checkStringsFile( dirFiles[i] );
            }
            else
            {
                if( dirFiles[i].getName().endsWith("_pt_PT.properties") )
                {
                    System.out.println( "----------------------------------------------------" );
                    System.out.println( dirFiles[i].getAbsolutePath() );

                    FileInputStream oSourcePtInputStream = new FileInputStream( dirFiles[i] );

                    File oSourceOtherFile = new File( dirFiles[i].getAbsolutePath().replaceAll( "_pt_PT.properties","_es_ES.properties" ) );

                    if (!oSourceOtherFile.exists())
                    {
                        FileWriter oFileWriter = new FileWriter( oSourceOtherFile );
                        oFileWriter.close();

                    }

                    FileInputStream oSourceOtherInputStream = new FileInputStream( oSourceOtherFile );

                    Properties oPropertiesPt = new Properties();
                    oPropertiesPt.load( oSourcePtInputStream );


                    Properties oPropertiesOther = new Properties();
                    oPropertiesOther.load( oSourceOtherInputStream );

                    oSourceOtherInputStream.close();
                    oSourcePtInputStream.close();

                    String key;

                    java.util.Enumeration oEnum = oPropertiesPt.keys();
                    while( oEnum.hasMoreElements() )
                    {
                        key = oEnum.nextElement().toString();
                        if( !oPropertiesOther.containsKey( key ) )
                        {
                            oPropertiesOther.put( key, oPropertiesPt.getProperty( key ) + "$PT$" );
                        }
                    }

                    saveProperties( oPropertiesOther, oSourceOtherFile );
                }
            }
        }
    }


    public static final void saveProperties( Properties oOutProperties, File oFile ) throws Exception
    {
        Vector      keys = new Vector();
        Enumeration oEnum = oOutProperties.keys();

        String key;
        while( oEnum.hasMoreElements() )
        {
            key = oEnum.nextElement().toString();
            keys.add( key );
        }

        String sOrderKeys[] = (String[])keys.toArray(new String[ keys.size() ]);
        Arrays.sort( sOrderKeys, new Comparator()
            {
                public int compare( Object l, Object r)
                {
                    if( l.toString().indexOf('.') > -1 && l.toString().indexOf('.') > -1 )
                    {
                        String s1 = l.toString().substring( 0, l.toString().indexOf('.') + 1 );
                        String s2 = l.toString().substring( l.toString().indexOf('.') + 1 );
                        String s3 = StringUtils.padl( s2, 5, "0" );

                        String s11 = r.toString().substring( 0, r.toString().indexOf('.') + 1 );
                        String s12 = r.toString().substring( r.toString().indexOf('.') + 1 );
                        String s13 = StringUtils.padl( s12, 5, "0" );

                        return (s1+s3).compareTo( s11 + s13 );


                    }
                    else
                    {
                        return l.toString().compareTo( String.valueOf( r ) );
                    }
                }
            }
        );

        FileWriter fw = new FileWriter( oFile );
        PrintWriter pw = new PrintWriter( fw );

        for (int i = 0; i < sOrderKeys.length; i++)
        {
            String value = oOutProperties.getProperty( sOrderKeys[i] );

            value = value.replaceAll( "=","\\\\=" );

            pw.print( sOrderKeys[i] );
            pw.print( '=' );
            pw.println( value );

        }

        pw.close();
        fw.close();
    }


}