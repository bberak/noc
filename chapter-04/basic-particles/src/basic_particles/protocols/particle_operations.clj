(ns basic-particles.protocols.particle-operations)

(defprotocol ParticleOperations
  (update-particle [p [& forces]])
  (render-particle [p])
  (is-alive? [p]))