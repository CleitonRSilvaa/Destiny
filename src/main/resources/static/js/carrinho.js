let carrinho = JSON.parse(localStorage.getItem("carrinho")) || [];

document.addEventListener("DOMContentLoaded", () => {
  atualizaVisualizacaoCarrinho();
});

$(document).ready(function () {
  $("#cep").mask("00000-000");
});

function showError(inputId, message) {
  if (inputId === "cep") {
    const inputElement = document.getElementById("dados-enredeco");
    inputElement.classList.add("border-2", "border-red-500"); // Adiciona uma borda vermelha

    const errorDiv = document.createElement("div");
    errorDiv.textContent = message;
    errorDiv.classList.add("text-red-500", "text-sm", "mt-1"); // Classe de erro para a mensagem
    inputElement.parentNode.insertBefore(errorDiv, inputElement.nextSibling);
  } else {
    const inputElement = document.getElementById(inputId);
    inputElement.classList.add("border-2", "border-red-500"); // Adiciona uma borda vermelha

    const errorDiv = document.createElement("div");
    errorDiv.textContent = message;
    errorDiv.classList.add("text-red-500", "text-sm", "mt-1"); // Classe de erro para a mensagem
    inputElement.parentNode.insertBefore(errorDiv, inputElement.nextSibling);
  }
}

function gerarValoresFrete(cep) {
  // Simula a lógica de determinação de valores de frete baseados na faixa do CEP
  const freteBase = parseInt(cep.slice(0, 5), 10) % 3; // Um exemplo simples baseado nos 5 primeiros dígitos do CEP
  const valoresFrete = [
    { tipo: "padrão", prazo: 7, custo: (freteBase + 1) * 10 }, // R$ 10, 20 ou 30
    { tipo: "expressa", prazo: 3, custo: (freteBase + 3) * 10 }, // R$ 30, 40 ou 50
    { tipo: "agendada", prazo: 10, custo: (freteBase + 5) * 10 }, // R$ 50, 60 ou 70
  ];

  const selectFrete = document.getElementById("frete");
  selectFrete.innerHTML = ""; // Limpa as opções existentes

  valoresFrete.forEach((opcaoFrete) => {
    const option = document.createElement("option");
    option.classList.add("text-gray-700");
    option.value = opcaoFrete.custo;
    option.textContent = `Entrega ${opcaoFrete.tipo} (até ${
      opcaoFrete.prazo
    } dias) - R$ ${opcaoFrete.custo.toFixed(2)}`;
    selectFrete.appendChild(option);
  });
}

// Você pode chamar essa função quando um usuário inserir um CEP, por exemplo:
// gerarValoresFrete('12345-678');

function resetMessages() {
  // Remove as mensagens de erro anteriores e as bordas vermelhas
  const errorMessages = document.querySelectorAll(".text-red-500");
  errorMessages.forEach((message) => message.remove());

  const errorInputs = document.querySelectorAll(".border-red-500");
  errorInputs.forEach((input) => input.classList.remove("border-red-500"));
  errorInputs.forEach((input) => input.classList.remove("border-2"));
}
const fetchAddress = () => {
  let cep = document.getElementById(`cep`);
  resetMessages();
  if (cep.value.trim() === "") {
    showError("cep", "Informe um CEP");
    return;
  }
  if (cep.value.length !== 9) {
    showError("cep", "CEP icompleto");
    return;
  }

  fetch(`https://viacep.com.br/ws/${cep.value}/json/`)
    .then((response) => response.json())
    .then((data) => {
      if (data.erro) {
        toggleModal("popup-modal-error", "", true, "CEP não encontrado!");
      } else {
        let divEnderec = document.getElementById("dados-enredeco-data");
        divEnderec.classList.remove("hidden");

        let p = divEnderec.querySelector("p") || document.createElement("p");

        // Define o conteúdo do parágrafo com os dados da API
        p.textContent = `${data.logradouro}, ${data.bairro} -  ${data.uf}`;

        // Adiciona o parágrafo na div se ele foi recém-criado
        if (!divEnderec.contains(p)) {
          divEnderec.appendChild(p);
        }

        console.log(cep.value.replace("-", ""));
        gerarValoresFrete(cep.value.replace("-", ""));
      }
    })
    .catch(() => {
      toggleModal("popup-modal-error", "", true, "Erro ao buscar o CEP!");
    });
};

function adicionarAoCarrinho(btn) {
  console.log("Antes de desativar:", btn.disabled);
  btn.disabled = true;

  const produtoId = document.getElementById("produto-id").value;
  const estoque = parseInt(document.getElementById("produto-quantidade").value);
  const nome = document.getElementById("produto-nome").value;
  const valor = parseFloat(document.getElementById("produto-valor").value);
  const quantidade = parseInt(document.getElementById("quantidade").value);
  const imagem = document.getElementById("imgPrincipal").getAttribute("src");

  let produto = {
    id: produtoId,
    nome: nome,
    valor: valor,
    quantidade: quantidade,
    estoque: estoque,
    subTotal: 0,
    img: imagem,
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

  if (itemExistente.quantidade >= itemExistente.estoque) {
    exibirModalNotificacao(
      "Quantidade solicitada não disponível no estoque.",
      2,
      1900
    );
    return;
  }

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

function toggleModal(modalID, value, erro = false, texto = "") {
  if (erro) {
    document.getElementById(modalID).classList.toggle("hidden");
    document.getElementById("teste-erro").innerText = texto;
    document.getElementById(modalID + "-backdrop").classList.toggle("hidden");
    document.getElementById(modalID + "-backdrop").classList.toggle("flex");
  } else {
    document.getElementById(modalID).classList.toggle("hidden");
    document.getElementById("id-item-remove").value = value;
    document.getElementById(modalID + "-backdrop").classList.toggle("hidden");
    document.getElementById(modalID + "-backdrop").classList.toggle("flex");
  }
}

function adicionaAoCarrinhoLogic(produto, btn) {
  let itemExistente = carrinho.find((item) => item.id === produto.id);

  if (itemExistente) {
    if (produto.quantidade + itemExistente.quantidade > produto.estoque) {
      exibirModalNotificacao(
        "Quantidade solicitada não disponível no estoque.",
        2,
        1900
      );

      return;
    }
    itemExistente.quantidade += produto.quantidade;
    itemExistente.subTotal = itemExistente.quantidade * itemExistente.valor;
  } else {
    if (produto.quantidade > produto.estoque) {
      exibirModalNotificacao(
        "Quantidade solicitada não disponível no estoque.",
        2,
        1900
      );
      return;
    }
    produto.subTotal = produto.quantidade * produto.valor;
    carrinho.push(produto);
  }

  //btn.disabled = false;

  exibirModalNotificacao("Produto adicionado ao carrinho!", 1);
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

  calculaFreteClienteLogado();
  document.getElementById("carrinhoCount").textContent = totalItens;

  return;
}

function limpaCarrinho() {
  carrinho = [];
  atualizaVisualizacaoCarrinho();
  atualizaLocalStorage();
  location.reload();
}

function exibirModalNotificacao(text = "", tipo = 1, time = 1000) {
  const modal = document.getElementById("modalNotification");
  const modalText = document.getElementById("modalNotification-text");

  const boterTipo = {
    1: "border-green-400",
    2: "border-red-400",
  };

  modalText.innerText = text;
  modal.classList.remove("hidden");
  modal.classList.add(boterTipo[tipo]);

  setTimeout(() => {
    modal.classList.add("hidden");
    modal.classList.remove(boterTipo[tipo]);
  }, time); // O modal será ocultado após 3 segundos
}

function salvarPedidoNaSessao(pedido) {
  localStorage.setItem("pedido", JSON.stringify(pedido));
  return;
}

function calculaFreteClienteLogado() {
  if (document.querySelector("#endereco") !== null) {
    const pai = document.getElementById("detalhesValores");
    const filho = pai.querySelector("#endereco");
    if (filho !== null) {
      cep = document
        .getElementById("dados-enredeco-data")
        .getAttribute("data-cep")
        .replace("-", "");

      console.info(cep);
      gerarValoresFrete(cep);
    }
  }
}

function recuperarPedidoDaSessao() {
  var pedidoRecuperado = JSON.parse(localStorage.getItem("pedido"));
  return pedidoRecuperado;
}

var pedidoAtual = recuperarPedidoDaSessao();

function fazerPedido() {
  pedido = {
    clienteID: "",
    enderecoID: "",
    valorTotal: "",
    metodoPagamento: "",
    items: [],
    status: "",
    frete: 0,
  };
  let frete = parseFloat(document.getElementById("frete").value);

  pedido.frete = frete;

  salvarPedidoNaSessao(pedido);

  window.location.href = "/checkout";
}
