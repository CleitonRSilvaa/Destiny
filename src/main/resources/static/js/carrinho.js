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

  // Assumindo que o elemento img tem um ID 'imgPrincipal'
  let imagem = document.getElementById("imgPrincipal");

  // Pegar o valor do atributo 'src'
  const img = imagem.getAttribute("src");

  let produto = {
    id: produtoId,
    nome: nome,
    valor: valor,
    avaliacao: avaliacao,
    descricao: descricao,
    quantidade: quantidade,
    subTotal: 0,
    img: img,
  };
  console.log("Depois de desativar:", btn.disabled);
  adicionaAoCarrinhoLogic(produto, btn);
  btn.disabled = false;
}

function removerItem(id) {
  let itemExistente = carrinho.find((item) => item.id === String(id));
  if (itemExistente) {
    carrinho = carrinho.filter((item) => item.id !== String(id));
  }
  atualizaVisualizacaoCarrinho();
  atualizaLocalStorage();
  location.reload();
}

function removerItemModalConfirme() {
  removerItem(document.getElementById("id-item-remove").value);
}

function aumentarQuantidade(id) {
  let itemExistente = carrinho.find((item) => item.id === String(id));
  if (itemExistente) {
    itemExistente.quantidade += 1;
    itemExistente.subTotal = itemExistente.quantidade * itemExistente.valor;
  }
  atualizaVisualizacaoCarrinho();
  atualizaLocalStorage();
  location.reload();
}

function diminuirQuantidade(id) {
  let itemExistente = carrinho.find((item) => item.id === String(id));
  if (itemExistente) {
    if (itemExistente.quantidade == 1) {
      toggleModal("popup-modal", String(id));
      return;
    }
    itemExistente.quantidade -= 1;
    itemExistente.subTotal = itemExistente.quantidade * itemExistente.valor;
  }
  atualizaVisualizacaoCarrinho();
  atualizaLocalStorage();
  location.reload();
}

function toggleModal(modalID, value) {
  document.getElementById(modalID).classList.toggle("hidden");
  document.getElementById("id-item-remove").value = value;
  document.getElementById(modalID + "-backdrop").classList.toggle("hidden");
  document.getElementById(modalID + "-backdrop").classList.toggle("flex");
}

function adicionaAoCarrinhoLogic(produto, btn) {
  let itemExistente = carrinho.find((item) => item.id === produto.id);

  if (itemExistente) {
    itemExistente.quantidade += produto.quantidade;
    itemExistente.subTotal = itemExistente.quantidade * itemExistente.valor;
  } else {
    produto.subTotal = produto.quantidade * produto.valor;
    carrinho.push(produto);
  }

  //btn.disabled = false;
  exibirModalNotificacao();
  // Aqui você pode adicionar uma lógica para atualizar a visualização do carrinho na tela, se necessário.
  // Por exemplo, mostrando um número indicando quantos itens estão no carrinho.
  atualizaVisualizacaoCarrinho();

  atualizaLocalStorage();
  return;
}

function exibirPopup() {
  const popup = document.getElementById("alertPopup");
  popup.classList.remove("hidden");

  setTimeout(() => {
    popup.classList.add("hidden");
  }, 3000); // O popup será ocultado após 3 segundos

  return;
}

function atualizaLocalStorage() {
  // btn.disabled = false;
  localStorage.setItem("carrinho", JSON.stringify(carrinho));
  return;
}

function atualizaVisualizacaoCarrinho() {
  console.info(carrinho);

  const totalItens = carrinho.length;
  // const totalItens = carrinho.reduce(
  //   (acc, produto) => acc + produto.quantidade,
  //   0
  // );
  const totalValor = carrinho.reduce(
    (acc, produto) => acc + produto.quantidade * produto.valor,
    0
  );

  document.getElementById("carrinhoCount").textContent = totalItens;
  return;
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
  const modal = document.getElementById("modalNotification");
  modal.classList.remove("hidden");

  setTimeout(() => {
    modal.classList.add("hidden");
  }, 1000); // O modal será ocultado após 3 segundos
}
