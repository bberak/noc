(ns advanced-particles.protocols.particle)

(defprotocol Particle
  (step [p [& forces]])
  (render [p])
  (is-alive? [p]))