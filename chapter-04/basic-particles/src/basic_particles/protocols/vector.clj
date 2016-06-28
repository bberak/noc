(ns basic-particles.protocols.vector)

(defprotocol Vector
  (multiply [v n])
  (divide [v n])
  (add [v1 v2])
  (subtract [v1 v2])
  (magnitude [v])
  (normalize [v]))