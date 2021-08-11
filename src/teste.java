
import br.com.grupocampanha.xml.Node;
import br.com.grupocampanha.xml.XML;
import br.com.grupocampanha.xml.exceptions.IllegalNodePropetyNameException;
import br.com.grupocampanha.xml.exceptions.IllegalNodePropetyValueException;
import br.com.grupocampanha.xml.exceptions.InsertNodeException;
import br.com.grupocampanha.xml.exceptions.InsertNodeValueException;
import br.com.grupocampanha.xml.exceptions.UnformattedXmlException;
import java.io.File;
import java.io.IOException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jfc
 */
public class teste {
    public static void main(String[] args) throws IOException, InsertNodeException, InsertNodeValueException, UnformattedXmlException, IllegalNodePropetyNameException, IllegalNodePropetyValueException{
    Node tete = XML.parse(new File("C:\\Users\\victor\\Desktop\\teste  2\\config.xml"));

        System.out.println(tete);
     
    }
}
