(ns basic-particles.protocols.particle-operations)

(defprotocol ParticleOperations
  (update-particle [p [& forces]])
  (render [p])
  (is-alive? [p]))