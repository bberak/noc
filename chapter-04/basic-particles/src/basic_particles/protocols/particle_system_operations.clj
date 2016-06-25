(ns basic-particles.protocols.particle-system-operations)

(defprotocol ParticleSystemOperations
  (update-particles [ps [& forces]])
  (add-particles [ps [& new-particles]])
  (render-system [ps]))