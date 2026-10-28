(function () {
  "use strict";

  var screen = document.querySelector(".screen--ped");
  if (!screen) return;

  var stepProgress    = parseFloat(screen.dataset.stepProgress    || "0");
  var distanceProgress = parseFloat(screen.dataset.distanceProgress || "0");

  var stepsBar = document.getElementById("ped-steps-bar");
  var kmBar    = document.getElementById("ped-km-bar");

  /* Animate bars on load (short delay so the CSS transition is visible). */
  requestAnimationFrame(function () {
    requestAnimationFrame(function () {
      if (stepsBar) stepsBar.style.width = (stepProgress * 100).toFixed(2) + "%";
      if (kmBar)    kmBar.style.width    = (distanceProgress * 100).toFixed(2) + "%";
    });
  });

  /* Panel switching. */
  var tabs   = document.querySelectorAll(".ped-tab");
  var panels = document.querySelectorAll(".ped-panel");

  tabs.forEach(function (tab) {
    tab.addEventListener("click", function () {
      var target = tab.dataset.target;

      panels.forEach(function (p) {
        var show = p.dataset.panel === target;
        p.classList.toggle("ped-panel--hidden", !show);
        if (show) {
          /* Re-trigger entry animation */
          p.style.animation = "none";
          p.offsetHeight; /* reflow */
          p.style.animation = "";
        }
      });

      tabs.forEach(function (t) {
        var active = t.dataset.target === target;
        t.classList.toggle("is-active", active);
        t.setAttribute("aria-selected", active ? "true" : "false");
      });
    });
  });
})();
