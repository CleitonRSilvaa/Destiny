$(document).ready(function () {
  $("#cep").mask("00000-000");
  $("#card-no").mask("0000-0000-0000-0000");
  $("#codigo-cvc").mask("000");
});
let carrinho2 = JSON.parse(localStorage.getItem("carrinho")) || [];

var freteFinal = 0;
document.addEventListener("DOMContentLoaded", () => {
  verificarCarrinhoTamanho();
  addEventListeners();
  atualizaVisualizacaoCarrinho();
});

function verificarCarrinhoTamanho() {
  if (carrinho2.length === 0) {
    window.location.href = "/carrinho";
  }
}

function atualizaVisualizacaoCarrinho() {
  const totalItens = carrinho2.length;
  document.getElementById("carrinhoCount").textContent = totalItens;
  return;
}

function selectAddress(selectedIndex) {
  const addresses = document.querySelectorAll(".address-item");
  addresses.forEach((address) => {
    address.classList.remove("selected");
  });
  addresses[selectedIndex].classList.add("selected");
  calculaFreteClienteLogadoCheckout();
}

let carrinhoContainer = document.getElementById("produto-list");

// Itera sobre os itens do carrinho e cria a marcação HTML para cada um
carrinho2.forEach((item) => {
  let itemDiv = `
  <div class="flex items-center mb-5">
    <div class="w-1/6">
    <img src="${item.img}" alt="${item.nome}"
    class="w-full h-auto object-cover rounded">
    </div>
    <div class="w-6/6 pl-4">
      <h2 class="text-lg font-semibold">${item.nome}</h2>
      <p class="text-sm">Valor unitario: R$${item.valor.toFixed(2)}</p>
      <p class="text-sm">Quantidade: ${item.quantidade}</p>
      <p class="text-lg font-semibold">SubTotal: R$${item.subTotal.toFixed(
        2
      )}</p>
    </div>
  </div>
        
    `;
  // Adiciona o itemDiv no carrinhoContainer
  carrinhoContainer.insertAdjacentHTML("beforeend", itemDiv);
});

function toggleModal(modalID) {
  document.getElementById(modalID).classList.toggle("hidden");
  document.getElementById(modalID + "-backdrop").classList.toggle("hidden");
  document.getElementById(modalID + "-backdrop").classList.toggle("flex");
}

function togglePaymentMethod(method) {
  const pagamentos = document.querySelectorAll(".payment-item");
  var cardDetails = document.getElementById("card-details");
  pagamentos.forEach((pagamento) => {
    pagamento.classList.remove("selected");
    if (pagamento.id === method) {
      pagamento.classList.add("selected");
    }
  });

  if (method === "payment-card") {
    cardDetails.classList.remove("hidden");
  } else {
    cardDetails.classList.add("hidden");
  }
}

function handleEnderecoSave() {
  const endereco = {
    cep: document.getElementById("cep").value,
    logradouro: document.getElementById("logradouro").value,
    complemento: document.getElementById("complemento").value,
    bairro: document.getElementById("bairro").value,
    localidade: document.getElementById("localidade").value,
    uf: document.getElementById("uf").value,
    numero: document.getElementById("numero").value,
    principal: false,
    tipo: 0,
  };

  const validacaoErro = validarEndereco(endereco);
  if (validacaoErro) {
    Swal.fire("Erro!", validacaoErro, "warning");
    return;
  }
  salvarNovoEndereco(endereco);
}

async function salvarNovoEndereco(endereco) {
  try {
    const response = await fetch(`http://localhost:8080/cliente/add/endereco`, {
      method: "POST",
      body: JSON.stringify(endereco),
      headers: {
        "Content-Type": "application/json; charset=utf-8",
      },
    });
    const data = await response.json();
    if (data.message === "success") {
      Swal.fire("", "Endereço cadastrado com sucesso.", "success").then(() => {
        location.reload();
      });
    } else {
      Swal.fire(
        "Erro!",
        "Erro ao cadastrar novo endereço! Tente novamente mais tarde.",
        "error"
      );
    }
  } catch (error) {
    Swal.fire(
      "Erro!",
      "Erro ao cadastrar novo endereço! Tente novamente mais tarde.",
      "error"
    );
  }
}

function validarEndereco(endereco) {
  if (!endereco.cep || endereco.cep.length !== 9) return "CEP é obrigatório!";
  if (!endereco.logradouro) return "Logradouro é obrigatório!";
  if (!endereco.bairro) return "Bairro é obrigatório!";
  if (!endereco.localidade) return "Cidade é obrigatória!";
  if (!endereco.uf || endereco.uf.length !== 2) return "UF inválida!";
  if (!endereco.numero) return "Número é obrigatório!";
  return null;
}

const addEventListeners = () => {
  document.getElementById("cep").addEventListener("blur", handleCEPBlur());
};

const handleCEPBlur = () => (e) => {
  if (e.target.value.length == 9) {
    fetchAddress();
  }
};

function alterAllInputsClass(invert = false) {
  const div = document.getElementById(`deliveryAddressFieldsInfo`);
  const inputs = div.querySelectorAll("input");
  const pElements = div.querySelectorAll("p");

  if (invert) {
    inputs.forEach((input) => {
      input.classList.remove("has-ok");
      input.classList.add("has-error");
    });
    return;
  }
  pElements.forEach((p) => {
    p.remove();
  });
  inputs.forEach((input) => {
    if (input.value.trim()) {
      input.classList.remove("has-error");
      input.classList.add("has-ok");
    }
  });
}

const fetchAddress = () => {
  let cep = document.getElementById(`cep`);
  let addressFields = document.getElementById(`deliveryAddressFieldsInfo`);
  const existingError = cep.nextElementSibling;
  if (existingError && existingError.classList.contains("error-message")) {
    existingError.textContent = "";
  }
  fetch(`https://viacep.com.br/ws/${cep.value}/json/`)
    .then((response) => response.json())
    .then((data) => {
      if (data.erro) {
        alterAllInputsClass(true);
        addressFields.classList.remove("hidden");
        cep.classList.remove("has-error");
        alert("CEP não encontrado!");
      } else {
        document.getElementById(`logradouro`).value = data.logradouro;
        document.getElementById(`bairro`).value = data.bairro;
        document.getElementById(`localidade`).value = data.localidade;
        document.getElementById(`uf`).value = data.uf;
        cep.classList.remove("has-error");
        cep.classList.add("has-ok");
        alterAllInputsClass();
        addressFields.classList.remove("hidden");
      }
    })
    .catch(() => {
      alterAllInputsClass(true);
      addressFields.classList.remove("hidden");
      alert("Erro ao buscar o CEP!");
    });
};

var pedido = {
  clienteID: "",
  enderecoID: "",
  valorTotal: "",
  metodoPagamento: "",
  items: [],
  status: "",
};

function app() {
  return {
    step: 1,

    goToNextStep() {
      if (this.validateStep(this.step)) {
        this.step++;
      }
    },

    validateStep(step) {
      switch (step) {
        case 1:
          return this.validateStep1();
        case 2:
          if (this.validateStep2()) {
            this.stepResumo();
            return true;
          }
          return false;
        default:
          return;
      }
    },

    setErrorMessage: (input, message) => {
      if (message) {
        input.classList.remove("has-ok");
        input.classList.add("has-error");
      } else {
        input.classList.remove("has-error");
        input.classList.add("has-ok");
      }

      const existingError = input.nextElementSibling;

      if (existingError && existingError.classList.contains("error-message")) {
        existingError.textContent = message;
      } else {
        const p = document.createElement("p");
        p.classList.add("error-message");
        p.innerText = message;
        input.insertAdjacentElement("afterend", p);
      }
    },
    regexNumeroCpf: (dado) => {
      dado = dado.replace(/[^\d]+/g, "");
      return dado;
    },

    validateStep1: function () {
      resetMessages();

      var paragraph;
      let result = null;
      var addressItems = document.querySelectorAll(
        "#address-list .address-item"
      );

      for (let i = 0; i < addressItems.length; i++) {
        if (addressItems[i].classList.contains("selected")) {
          result = addressItems[i].getAttribute("data-id");
          paragraph = addressItems[i].querySelector("p");
          pedido.enderecoID = parseInt(result);
        }
      }

      let frete = document.getElementById("frete").value;

      if (frete.trim().length === 0) {
        showError("frete", "Selecione o tipo de entrega!");
        return false;
      }

      if (result === null) alert("Selecione um endereço de entrega!");

      document.getElementById("resumo-endereco").innerText =
        paragraph.innerText;

      freteFinal = frete;
      document.getElementById("resumo-frete").innerText =
        "R$ " + parseFloat(frete).toFixed(2);
      return result >= 0;
    },

    validateStep2: function () {
      resetMessages();
      let formaPag = {
        "payment-card": "CARTAO",
        pix: "PIX",
        boleto: "BOLETO",
      };
      let isValid = true;
      let result = null;
      let pagamentoItems = document.querySelectorAll(
        "#payment-list .payment-item"
      );

      for (let i = 0; i < pagamentoItems.length; i++) {
        if (pagamentoItems[i].classList.contains("selected")) {
          result = pagamentoItems[i].id;
        }
      }

      if (result === null) {
        alert("Selecione um metodo de pagamento!");
        return false;
      }

      if (result === "payment-card") {
        // Validação do nome no cartão
        const cardHolder = document.getElementById("card-holder");

        if (cardHolder.value.trim() === "") {
          showError(
            "card-holder",
            "Por favor, insira o nome impresso no cartão."
          );
          isValid = false;
        }

        // Validação do número do cartão
        const cardNo = document.getElementById("card-no");
        cardNo.value = cardNo.value.replace(/[^\d]+/g, "");
        if (!validateCardNumber(cardNo.value)) {
          showError("card-no", "Número do cartão inválido.");
          isValid = false;
        }

        // Validação da data de vencimento
        const expiryDate = document.getElementById("data-vencimento");
        if (!validateExpiryDate(expiryDate.value)) {
          showError(
            "data-vencimento",
            "Data de vencimento inválida ou expirada."
          );
          isValid = false;
        }

        // Validação do CVC
        const cvc = document.getElementById("codigo-cvc");
        if (!validateCVC(cvc.value)) {
          showError("codigo-cvc", "Código CVC inválido.");
          isValid = false;
        }
      }

      pedido.metodoPagamento = formaPag[result.toLowerCase()];
      document.getElementById("resumo-pagamento").innerText =
        formaPag[result.toLowerCase()];

      return result !== null && isValid;
    },

    stepResumo: function () {
      let idcliente = document
        .getElementById("cliente-id")
        .getAttribute("data_id");

      let subTotalValor = carrinho2.reduce(
        (acc, produto) => acc + produto.quantidade * produto.valor,
        0
      );

      let frete = parseFloat(freteFinal);
      let subTotal = parseFloat(subTotalValor);
      let total = subTotal + frete;

      pedido.clienteID = parseInt(idcliente);
      pedido.items = carrinho2;
      pedido.status = "PENDENTE";
      pedido.valorTotal = total;

      document.getElementById("resumo-subtotal").innerText =
        "R$ " + subTotal.toFixed(2);

      document.getElementById("resumo-total").innerText =
        "R$ " + total.toFixed(2);
      console.info(pedido);
      return true;
    },

    submitCompra: function () {
      document.getElementById("loader").style.display = "block";
      document.getElementById("btnComprar").disabled = true;
      document.getElementById("btnVolta").disabled = true;

      makeRequest(
        "POST",
        "/pedido/novo-pedido",
        pedido,
        "Compra efetuada com sucesso"
      );
      document.getElementById("loader").style.display = "none";
      document.getElementById("btnComprar").disabled = false;
      document.getElementById("btnVolta").disabled = false;
    },
  };
}

function validateCardNumber(number) {
  // Uma simples checagem para ver se o número contém 16 dígitos (Você pode expandir com expressões regulares para um padrão específico)
  return /^\d{16}$/.test(number.replace(/\s+/g, ""));
}

function validateExpiryDate(date) {
  // Checagem se a data está no formato AAAA-MM e se é uma data futura
  const currentDate = new Date();
  const currentYear = currentDate.getFullYear();
  const currentMonth = currentDate.getMonth() + 1; // getMonth retorna 0-11
  const [year, month] = date.split("-").map((part) => parseInt(part, 10));

  return year > currentYear || (year === currentYear && month >= currentMonth);
}

function validateCVC(cvc) {
  // Checagem se o CVC contém 3 ou 4 dígitos
  return /^\d{3,4}$/.test(cvc);
}

function showError(inputId, message) {
  const inputElement = document.getElementById(inputId);
  inputElement.classList.add("border-red-500"); // Adiciona uma borda vermelha

  const errorDiv = document.createElement("div");
  errorDiv.textContent = message;
  errorDiv.classList.add("text-red-500", "text-sm", "mt-1"); // Classe de erro para a mensagem
  inputElement.parentNode.insertBefore(errorDiv, inputElement.nextSibling);
}

function resetMessages() {
  // Remove as mensagens de erro anteriores e as bordas vermelhas
  const errorMessages = document.querySelectorAll(".text-red-500");
  errorMessages.forEach((message) => message.remove());

  const errorInputs = document.querySelectorAll(".border-red-500");
  errorInputs.forEach((input) => input.classList.remove("border-red-500"));
}

function makeRequest(e, t, a, i) {
  console.log(a),
    $.ajax({
      type: e,
      url: `http://localhost:8080${t}`,
      data: JSON.stringify(a),
      contentType: "application/json; charset=utf-8",
      success: function (e) {
        [200, 201].includes(e.status) && "sucess" === e.message
          ? Swal.fire({
              icon: "success",
              title: i,
              showConfirmButton: true,
              confirmButtonText: "OK",
            }).then((result) => {
              var inputs = document.querySelectorAll("input, textarea");
              inputs.forEach(function (input) {
                input.value = "";
              });
              limpaCarrinho();

              window.location.href = "/meus-pedidos";
            })
          : Swal.fire({
              icon: "error",
              title: "Erro",
              text: "Resposta inesperada do servidor. Por favor, tente novamente.",
            });
      },
      error: function (e) {
        let t = e.responseJSON,
          a = "<br/><br/>"; // Iniciei a variável com as quebras de linha

        if (t.details) {
          a += t.details.map((detail) => "-" + detail).join("<br/>"); // Adicionando detalhes com quebras de linha
        }

        Swal.fire({
          icon: "error",
          title: t.message, // Definindo t.message como o título
          html: a, // Configurando o conteúdo formatado em HTML como o corpo da mensagem
        });
      },
    });
}
function gerarValoresFrete2(cep) {
  const freteBase = parseInt(cep.slice(0, 5), 10) % 3; // Um exemplo simples baseado nos 5 primeiros dígitos do CEP
  const valoresFrete = [
    { tipo: "padrão", prazo: 7, custo: (freteBase + 1) * 10 }, // R$ 10, 20 ou 30
    { tipo: "expressa", prazo: 3, custo: (freteBase + 3) * 10 }, // R$ 30, 40 ou 50
    { tipo: "agendada", prazo: 10, custo: (freteBase + 5) * 10 }, // R$ 50, 60 ou 70
  ];

  const selectFrete = document.getElementById("frete");
  selectFrete.innerHTML = ""; // Limpa as opções existentes
  const option = document.createElement("option");
  option.classList.add("text-gray-700");
  selectFrete.appendChild(option);
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

function calculaFreteClienteLogadoCheckout() {
  var addressItems = document.querySelectorAll("#address-list .address-item");
  let cepSelect;
  for (let i = 0; i < addressItems.length; i++) {
    if (addressItems[i].classList.contains("selected")) {
      result = addressItems[i].getAttribute("data-id");
      cepSelect = addressItems[i].getAttribute("data-cep");
      gerarValoresFrete2(cepSelect.replace("-", ""));
    }
  }
}

window.onload = function () {
  calculaFreteClienteLogadoCheckout();
  var inputs = document.querySelectorAll("input, textarea");
  inputs.forEach(function (input) {
    input.value = "";
  });
};
