let carrinho = JSON.parse(localStorage.getItem("carrinho")) || [];

function adicionarAoCarrinho(btn) {

  console.log("Antes de desativar:", btn.disabled);
  btn.disabled = true;
  
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
  console.log("Depois de desativar:", btn.disabled);
  adicionaAoCarrinhoLogic(produto, btn);
 }

function adicionaAoCarrinhoLogic(produto,btn) {
  
  let itemExistente = carrinho.find((item) => item.id === produto.id);

  if (itemExistente) {
    itemExistente.quantidade += produto.quantidade;
    itemExistente.subTotal = itemExistente.quantidade * itemExistente.valor;
  } else {
    carrinho.push(produto);
  }

  //btn.disabled = false;
  exibirModalNotificacao();
  // Aqui você pode adicionar uma lógica para atualizar a visualização do carrinho na tela, se necessário.
  // Por exemplo, mostrando um número indicando quantos itens estão no carrinho.
  atualizaVisualizacaoCarrinho();

  atualizaLocalStorage(btn);
}

function exibirPopup() {
  const popup = document.getElementById("alertPopup");
  popup.classList.remove("hidden");

  setTimeout(() => {
    popup.classList.add("hidden");
  }, 3000);  // O popup será ocultado após 3 segundos
}

function atualizaLocalStorage(btn) {
  btn.disabled = false;
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

  document.getElementById("carrinhoCount").textContent = totalItens;
}

document.addEventListener("DOMContentLoaded", () => {
  atualizaVisualizacaoCarrinho();
});

function limpaCarrinho() {
  carrinho = [];
  atualizaVisualizacaoCarrinho();
  atualizaLocalStorage();
}


function exibirModalNotificacao() {
  const modal = document.getElementById('modalNotification');
  modal.classList.remove('hidden');

  setTimeout(() => {
    modal.classList.add('hidden');
  }, 1000);  // O modal será ocultado após 3 segundos
}
