(ns basic-particles.protocols.particle-system)

(defprotocol ParticleSystem
  (update-particles [ps [& forces]])
  (add-particles [ps [& new-particles]])
  (render-system [ps]))