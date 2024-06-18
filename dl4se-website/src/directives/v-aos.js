export default (el, binding) => {
  const config = binding.value || {};
  const modifiers = binding.modifiers || {};
  Object.entries({
    "data-aos": config.animation,
    "data-aos-offset": `${config.offset ?? 120}`,
    "data-aos-delay": `${config.delay ?? 0}`,
    "data-aos-duration": `${config.duration ?? 400}`,
    "data-aos-easing": config.easing || "ease",
    "data-aos-anchor-placement": config.anchorPlacement || "top-bottom",
    "data-aos-once": `${!!modifiers.once}`,
    "data-aos-mirror": `${!!modifiers.mirror}`,
  }).forEach(([key, value]) => el.setAttribute(key, value));
};
