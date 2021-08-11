
import br.com.grupocampanha.xml.Node;
import br.com.grupocampanha.xml.XML;
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
    public static void main(String[] args) throws IOException, InsertNodeException, InsertNodeValueException, UnformattedXmlException{
    Node tete = XML.parse(new File("C:\\Users\\jfc\\Desktop\\aaaaaa\\Config.xml"));
    Node[]  nodes= tete.findNodes(n -> n.getPropriedade("animal") != null);
        System.out.println(nodes);
    }
}
