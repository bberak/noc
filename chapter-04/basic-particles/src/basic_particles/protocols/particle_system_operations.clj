(ns basic-particles.protocols.particle-system-operations)

(defprotocol ParticleSystemOperations
  (update-system [ps location [& forces]])
  (render-system [ps]))