package netgest.bo.impl.document.print;
import java.io.File;
import java.io.FileFilter;

public class Filtro implements FileFilter
    {
        private String ext;
        public Filtro(String ext)
        {
            this.ext = ext;
        }
        public boolean accept(File pathname)
        {
            return pathname.getName().endsWith(this.ext);
        }
    }