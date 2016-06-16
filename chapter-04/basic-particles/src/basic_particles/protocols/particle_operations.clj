(ns basic-particles.protocols.particle-operations)

(defprotocol ParticleOperations
  (update-particle [p])
  (render [p])
  (is-alive? [p]))