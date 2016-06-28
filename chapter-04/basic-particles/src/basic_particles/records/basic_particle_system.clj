(ns basic-particles.records.basic-particle-system
  (:require [basic-particles.protocols.particle :refer :all]
            [basic-particles.protocols.particle-system :refer :all]))

(defrecord BasicParticleSystem [particles]
  
  ParticleSystem
  
  (update-particles [ps [& forces]]
  	(let [updated-particles (filter is-alive? (map (fn [p] (update-particle p forces)) particles))]
     (BasicParticleSystem. updated-particles)))
  
  (add-particles [ps [& new-particles]]
    (BasicParticleSystem. (apply conj particles new-particles)))
  
  (render-system [ps]
  	(doseq [p particles]
      (render-particle p))))