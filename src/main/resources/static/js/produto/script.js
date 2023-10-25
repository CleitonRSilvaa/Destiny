const carousel = document.querySelector("#carouselMain");
const thumbnails = document.querySelectorAll("#thumbnailContainer img");

thumbnails.forEach((thumbnail, index) => {
  thumbnail.addEventListener("click", () => {
    const carouselInstance = bootstrap.Carousel.getInstance(carousel);
    carouselInstance.to(index);

    thumbnails.forEach((thumb) => thumb.classList.remove("active"));
    thumbnail.classList.add("active");
  });
});

carousel.addEventListener("slid.bs.carousel", (event) => {
  thumbnails.forEach((thumb) => thumb.classList.remove("active"));
  thumbnails[event.to].classList.add("active");
});

// $(document).ready(function () {
//   $("#comprarBtn").click(function () {
//     alert("Produto adicionado ao carrinho!");
//   });
// });
