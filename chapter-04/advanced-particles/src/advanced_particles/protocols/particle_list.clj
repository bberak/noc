(ns advanced-particles.protocols.particle-list)

(defprotocol ParticleList
  (append [ps [& new-particles]]))