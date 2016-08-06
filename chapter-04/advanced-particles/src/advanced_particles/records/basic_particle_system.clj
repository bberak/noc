(ns advanced-particles.records.basic-particle-system
  (:require [advanced-particles.protocols.particle :refer :all]
            [advanced-particles.protocols.particle-list :refer :all]))

(defrecord BasicParticleSystem [particles]
  
  Particle
  
  (step [ps [& forces]]
  	(let [updated-particles (filter is-alive? (map (fn [p] (step p forces)) particles))]
     (BasicParticleSystem. updated-particles)))
  
  (render [ps]
  	(doseq [p particles]
      (render p)))
  
  (is-alive? [ps]
    (not (empty? (filter (fn [p] (is-alive? p)) particles)))))

(extend-type BasicParticleSystem
  
  ParticleList
  
  (append [ps [& new-particles]]
    (BasicParticleSystem. (apply conj (:particles ps) new-particles))))