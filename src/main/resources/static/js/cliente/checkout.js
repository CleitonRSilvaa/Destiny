$(document).ready(function () {
  $("#cep").mask("00000-000");
  $("#card-no").mask("0000-0000-0000-0000");
  $("#codigo-cvc").mask("000");
});
let carrinho = JSON.parse(localStorage.getItem("carrinho")) || [];

var freteFinal = 0;
document.addEventListener("DOMContentLoaded", () => {
  verificarCarrinhoTamanho();
  addEventListeners();
  atualizaVisualizacaoCarrinho();
});

function verificarCarrinhoTamanho() {
  if (carrinho.length === 0) {
    window.location.href = "/carrinho";
  }
}

function atualizaVisualizacaoCarrinho() {
  const totalItens = carrinho.length;
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
carrinho.forEach((item) => {
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

function textoAleatorio(tamanho) {
  const caracteres =
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
  let texto = "";

  for (let i = 0; i < tamanho; i++) {
    const indiceAleatorio = Math.floor(Math.random() * caracteres.length);
    texto += caracteres.charAt(indiceAleatorio);
  }

  return texto;
}

function numerosAleatorio(tamanho) {
  const caracteres = "1234567890";
  let texto = "";

  for (let i = 0; i < tamanho; i++) {
    const indiceAleatorio = Math.floor(Math.random() * caracteres.length);
    texto += caracteres.charAt(indiceAleatorio);
  }

  return texto;
}

function togglePaymentMethod(method) {
  const pagamentos = document.querySelectorAll(".payment-item");

  pagamentos.forEach((pagamento) => {
    pagamento.classList.remove("selected");
    if (pagamento.id === method) {
      pagamento.classList.add("selected");
    }
  });

  let cardDetails = document.getElementById("card-details");
  let pixDetails = document.getElementById("pix-details");
  let boletoDetails = document.getElementById("boleto-details");

  if (method === "payment-card") {
    cardDetails.classList.remove("hidden");
  } else {
    cardDetails.classList.add("hidden");
  }

  if (method === "pix") {
    // const qrcode = document.querySelector("#qrcode");
    const textoPix = textoAleatorio(100);

    let qrApiUrl = `https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=${encodeURIComponent(
      textoPix
    )}`;

    fetch(qrApiUrl)
      .then((response) => {
        if (response.ok) {
          document.getElementById("qrcode").src = qrApiUrl;
        } else {
          document.getElementById("qrcode").src = "/img/qr-code.png";
        }
      })
      .catch((error) => {
        console.error("Error gerar qr-code:", error);
        document.getElementById("qrcode").src = localImageSrc;
      });

    document.getElementById("pixCode").value = textoPix;

    pixDetails.classList.remove("hidden");
  } else {
    pixDetails.classList.add("hidden");
  }

  if (method === "boleto") {
    let numerosAleatorioGerado = numerosAleatorio(48);

    JsBarcode("#codBarras", numerosAleatorioGerado);

    document.getElementById("numeroCodeBar").value = numerosAleatorioGerado;

    boletoDetails.classList.remove("hidden");
  } else {
    boletoDetails.classList.add("hidden");
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
    const response = await fetch(
      `https://destinyproject.azurewebsites.net/cliente/add/endereco`,
      {
        method: "POST",
        body: JSON.stringify(endereco),
        headers: {
          "Content-Type": "application/json; charset=utf-8",
        },
      }
    );
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
  enderecoEntregaId: "",
  clienteId: "",
  valorTotal: "",
  metodoPagamento: "",
  parcelas: 1,
  itemsPedido: [],
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
          pedido.enderecoEntregaId = parseInt(result);
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

      if (pedido.metodoPagamento === "CARTAO") {
        pedido.parcelas = document.getElementById("parcelas").value;
      }
      parcelasDetails = document.getElementById("parcelasInfo");
      pedido.metodoPagamento === "CARTAO"
        ? parcelasDetails.classList.remove("hidden")
        : parcelasDetails.classList.add("hidden");

      document.getElementById("resumo-pagamento").innerText =
        formaPag[result.toLowerCase()];

      return result !== null && isValid;
    },

    stepResumo: function () {
      let idcliente = document
        .getElementById("cliente-id")
        .getAttribute("data_id");

      let subTotalValor = carrinho.reduce(
        (acc, produto) => acc + produto.quantidade * produto.valor,
        0
      );

      let frete = parseFloat(freteFinal);
      let subTotal = parseFloat(subTotalValor);
      let total = subTotal + frete;

      const listaDeObjetos = [];
      carrinho.forEach((produto, index) => {
        const objeto = {
          produtoId: produto.id,
          nome: produto.nome,
          valor: produto.valor.toFixed(2),
          quantidade: produto.quantidade,
          img: produto.img,
        };
        listaDeObjetos.push(objeto);
      });

      pedido.clienteId = parseInt(idcliente);
      pedido.valorFrete = frete;
      pedido.itemsPedido = listaDeObjetos;
      pedido.valorTotal = total.toFixed(2);

      document.getElementById("resumo-subtotal").innerText =
        "R$ " + subTotal.toFixed(2);

      document.getElementById("resumo-total").innerText =
        "R$ " + total.toFixed(2);

      document.getElementById("resumo-percelas").innerText =
        pedido.parcelas.toString() +
        "x de R$ " +
        (total / pedido.parcelas).toFixed(2).toString();
      console.info(pedido);
      return true;
    },

    submitCompra: async function () {
      document.getElementById("loader").style.display = "block";
      document.getElementById("btnComprar").disabled = true;
      document.getElementById("btnVolta").disabled = true;

      await makeRequest("POST", "/pedido", pedido, "Pedido gerado com sucesso");
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

async function makeRequest(e, t, a, i) {
  console.info(a);
  try {
    const response = await fetch(
      "https://destinyproject.azurewebsites.net" + t,
      {
        method: e,
        body: JSON.stringify(a),
        headers: {
          "Content-Type": "application/json; charset=utf-8",
        },
      }
    );
    const data = await response.json();

    console.log(response.status);
    [200, 201].includes(response.status)
      ? respostaOK(data, i)
      : respostaErro(data);
  } catch (error) {
    respostaErro(error);
  }
}

function limpaCarrinho() {
  carrinho = [];
  atualizaVisualizacaoCarrinho();
  atualizaLocalStorage();
}
function respostaOK(e, i) {
  console.log(e);

  Swal.fire({
    icon: "success",
    title: i,
    text:
      "Obrigado por fazer uma compra conosco! Seu número de pedido é #" +
      e.numeroPedido +
      " no valor de de: R$" +
      e.valorTotal.toFixed(2) +
      " reais. " +
      "Por favor, mantenha este número seguro para referência futura. Se você tiver alguma dúvida ou precisar de ajuda, não hesite em entrar em contato conosco. Estamos sempre aqui para ajudá-lo. 😊",
    showConfirmButton: true,
    confirmButtonText: "OK",
  }).then((result) => {
    var inputs = document.querySelectorAll("input, textarea");
    inputs.forEach(function (input) {
      input.value = "";
    });
    limpaCarrinho();

    window.location.href = "/cliente/meus-pedidos";
  });
}

function respostaErro(e) {
  console.error(e);
  Swal.fire(
    "Opoos!",
    "Infelizmente não foi possível finalizar seu pedido. Tente novamente mais tarde. Se o problema persistir, entre em contato conosco para obter assistência 😊",
    "warning"
  );
}

function atualizaLocalStorage() {
  // btn.disabled = false;
  localStorage.setItem("carrinho", JSON.stringify(carrinho));
  return;
}

function erroAll(e) {
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
