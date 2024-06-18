export const VAos = (el, binding) => {
  const config = binding.value || {};
  const modifiers = binding.modifiers || {};
  Object.assign(el.dataset, {
    aos: config.animation,
    aosOffset: `${config.offset ?? 120}`,
    aosDelay: `${config.delay ?? 0}`,
    aosDuration: `${config.duration ?? 400}`,
    aosEasing: config.easing || "ease",
    aosAnchorPlacement: config.anchorPlacement || "top-bottom",
    aosOnce: `${!!modifiers.once}`,
    aosMirror: `${!!modifiers.mirror}`,
  });
};
