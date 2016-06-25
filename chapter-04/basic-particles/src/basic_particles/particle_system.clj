(ns basic-particles.particle-system
  (:require [quil.core :as q]
            [basic-particles.particle]
            [basic-particles.vector]
            [basic-particles.protocols.particle-operations :refer :all]
            [basic-particles.protocols.particle-system-operations :refer :all])
  (:import [basic_particles.particle Particle]
           [basic_particles.vector Vector]))

(defrecord ParticleSystem [particles]
  
  ParticleSystemOperations
  
  (update-system [ps location [& forces]]
  	(let [updated-particles (filter is-alive? (map (fn [p] (update-particle p forces)) particles))]
     (ParticleSystem. (if (q/mouse-pressed?) 
                        (conj updated-particles (Particle. location (Vector. (q/random -1 1) (q/random -7 0)) 255 0))
                        updated-particles))))
  
  (render-system [ps]
  	(doseq [p particles]
      (render-particle p))))