/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.grupocampanha.xml;

import br.com.grupocampanha.xml.interfaces.NodeValue;

/**
 *
 * @author jfc
 */
public class NodeContent implements NodeValue {

    String content;
    private NodeType nodeType;

    public NodeContent(String content, NodeType tipo) {
        if (tipo != NodeType.VALUE && tipo != NodeType.SPECIAL)
            throw new RuntimeException("Esse tipo de node nao e suportado aqui");
        this.content = content;
        this.nodeType = tipo;
    }

    @Override
    public String getValue() {
        return nodeType.equals(NodeType.VALUE)?content: "<"+content+">";
    }

    public NodeType getNodeType() {
        return nodeType;
    }
    
    /**
     * o mesmo que chamar  getValue();
     *     
     * @return 
     */
    @Override
    public String toString(){
        return getValue();
    }
}
