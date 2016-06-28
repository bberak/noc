(ns basic-particles.protocols.particle)

(defprotocol Particle
  (update-particle [p [& forces]])
  (render-particle [p])
  (is-alive? [p]))