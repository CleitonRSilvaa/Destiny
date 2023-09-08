package com.destiny.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller()
@RequestMapping("/produto")
public class ProdutoController {
    
    @GetMapping("/listarProduto") 
    public String telaProduto(){
        return "estoque/produtos.html"; // Não inclua a barra inicial e a extensão .html
    }

    @GetMapping("/addProduto")
    public String telaCadastro(Model model){


        model.getAttribute("nomeProduto");
        model.getAttribute("descricaoProduto");
        model.getAttribute("valor");
        model.getAttribute("qntProduto");
        model.getAttribute("");
        model.getAttribute("");
        
        return "estoque/addProduto.html";
    }




}