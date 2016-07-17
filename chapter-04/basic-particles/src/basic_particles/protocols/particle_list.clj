(ns basic-particles.protocols.particle-list)

(defprotocol ParticleList
  (append [ps [& new-particles]]))