document.addEventListener("DOMContentLoaded", function () {
  atualizaVisualizacaoCarrinho();
  // Toggle do menu mobile
  let menuButton = document.querySelector('[aria-controls="mobile-menu"]');
  let mobileMenu = document.getElementById("mobile-menu");

  // Esconder o menu móvel inicialmente
  mobileMenu.classList.add("hidden");

  menuButton.addEventListener("click", function () {
    mobileMenu.classList.toggle("hidden");
  });

  // Toggle do dropdown de perfil
  let userMenuButton = document.getElementById("user-menu-button");
  let userMenuDropdown = document.querySelector(
    '[aria-labelledby="user-menu-button"]'
  );

  // Esconder o dropdown do perfil inicialmente
  userMenuDropdown.classList.add("hidden");

  userMenuButton.addEventListener("click", function () {
    userMenuDropdown.classList.toggle("hidden");
  });
});

function atualizaVisualizacaoCarrinho() {
  let carrinho = JSON.parse(localStorage.getItem("carrinho")) || [];
  const totalItens = carrinho.length;
  document.getElementById("carrinhoCount").textContent = totalItens;
  return;
}
