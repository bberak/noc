(ns basic-particles.particle-system
  (:require [quil.core :as q]
            [basic-particles.particle]
            [basic-particles.protocols.particle-operations :refer :all]
            [basic-particles.protocols.particle-system-operations :refer :all])
  (:import [basic_particles.particle Particle]))

(defrecord ParticleSystem [particles]
  
  ParticleSystemOperations
  
  (update-particles [ps [& forces]]
  	(let [updated-particles (filter is-alive? (map (fn [p] (update-particle p forces)) particles))]
     (ParticleSystem. updated-particles)))
  
  (add-particles [ps [& new-particles]]
    (ParticleSystem. (apply conj particles new-particles)))
  
  (render-system [ps]
  	(doseq [p particles]
      (render-particle p))))