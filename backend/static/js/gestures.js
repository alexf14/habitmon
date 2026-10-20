/*
 * Gestos horizontales entre pantallas con Pointer Events (ratón y táctil).
 *
 * <main class="screen" data-swipe-left="/url" data-swipe-right="/url" data-scroller="#lista">
 *   - data-swipe-left:  destino al deslizar de derecha a izquierda (o tecla →).
 *   - data-swipe-right: destino al deslizar de izquierda a derecha (o teclas ← / Esc).
 *   - data-scroller:    contenedor con scroll vertical; si se ha desplazado durante el gesto,
 *                       el gesto se ignora para no cambiar de pantalla por accidente.
 */
(function () {
  "use strict";

  var MIN_DISTANCE = 60; // px en horizontal
  var RATIO = 2;         // |deltaX| debe ser al menos el doble que |deltaY|
  var LEAVE_MS = 160;

  var screen = document.querySelector(".screen");
  if (!screen) return;

  var targets = { left: screen.dataset.swipeLeft, right: screen.dataset.swipeRight };
  if (!targets.left && !targets.right) return;
  var scroller = screen.dataset.scroller ? document.querySelector(screen.dataset.scroller) : null;

  var leaving = false;
  function navigate(direction) {
    var url = targets[direction];
    if (!url || leaving) return;
    leaving = true;
    screen.classList.add("is-leaving-" + direction);
    setTimeout(function () { window.location.href = url; }, LEAVE_MS);
  }

  var start = null;
  var suppressClickUntil = 0;

  screen.addEventListener("pointerdown", function (e) {
    if (e.button > 0) return;
    start = { x: e.clientX, y: e.clientY, scroll: scroller ? scroller.scrollTop : 0 };
  });

  screen.addEventListener("pointercancel", function () { start = null; });

  screen.addEventListener("pointerup", function (e) {
    if (!start) return;
    var deltaX = e.clientX - start.x;
    var deltaY = e.clientY - start.y;
    var scrolled = scroller && Math.abs(scroller.scrollTop - start.scroll) > 4;
    start = null;

    if (scrolled) return;
    if (Math.abs(deltaX) < MIN_DISTANCE || Math.abs(deltaX) < RATIO * Math.abs(deltaY)) return;

    suppressClickUntil = Date.now() + 400; // el arrastre no debe contar como pulsación
    navigate(deltaX < 0 ? "left" : "right");
  });

  screen.addEventListener("click", function (e) {
    if (Date.now() < suppressClickUntil) { e.preventDefault(); e.stopPropagation(); }
  }, true);

  document.addEventListener("keydown", function (e) {
    if (document.querySelector("dialog[open]")) return;
    if (e.key === "ArrowRight") navigate("left");
    if (e.key === "ArrowLeft" || e.key === "Escape") navigate("right");
  });
})();
