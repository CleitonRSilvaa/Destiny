let carrinho = JSON.parse(localStorage.getItem("carrinho")) || [];

function adicionarAoCarrinho(btn) {
  const produtoId = btn.getAttribute("data-id");
  const nome =
    btn.previousElementSibling.previousElementSibling.previousElementSibling.previousElementSibling.previousElementSibling.getAttribute(
      "data-nome"
    );
  const valor = parseFloat(
    btn.previousElementSibling.previousElementSibling.previousElementSibling.previousElementSibling.getAttribute(
      "data-valor"
    )
  );
  const avaliacao = parseFloat(
    btn.previousElementSibling.previousElementSibling.previousElementSibling.getAttribute(
      "data-avaliacao"
    )
  );
  const descricao =
    btn.previousElementSibling.previousElementSibling.getAttribute(
      "data-descricao"
    );
  const quantidade = parseInt(document.getElementById("quantidade").value);

  let produto = {
    id: produtoId,
    nome: nome,
    valor: valor,
    avaliacao: avaliacao,
    descricao: descricao,
    quantidade: quantidade,
    subTotal: 0,
  };

  adicionaAoCarrinhoLogic(produto);
}

function adicionaAoCarrinhoLogic(produto) {
  let itemExistente = carrinho.find((item) => item.id === produto.id);

  if (itemExistente) {
    itemExistente.quantidade += produto.quantidade;
    itemExistente.subTotal = itemExistente.quantidade * itemExistente.valor;
  } else {
    carrinho.push(produto);
  }

  // Aqui você pode adicionar uma lógica para atualizar a visualização do carrinho na tela, se necessário.
  // Por exemplo, mostrando um número indicando quantos itens estão no carrinho.
  atualizaVisualizacaoCarrinho();

  atualizaLocalStorage();
}

function atualizaLocalStorage() {
  localStorage.setItem("carrinho", JSON.stringify(carrinho));
}

function atualizaVisualizacaoCarrinho() {
  console.info(carrinho);
  const totalItens = carrinho.reduce(
    (acc, produto) => acc + produto.quantidade,
    0
  );
  const totalValor = carrinho.reduce(
    (acc, produto) => acc + produto.quantidade * produto.valor,
    0
  );
  console.info(totalItens);
  console.info(totalValor);

  document.getElementById("carrinhoCount").textContent = totalItens;
  document.getElementById("carrinhoCount2").textContent = totalItens;
}

document.addEventListener("DOMContentLoaded", () => {
  atualizaVisualizacaoCarrinho();
});

function limpaCarrinho() {
  carrinho = [];
  atualizaVisualizacaoCarrinho();
  atualizaLocalStorage();
}
