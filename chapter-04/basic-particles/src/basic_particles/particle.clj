(ns basic-particles.particle
  (:require [quil.core :as q]
            [basic-particles.vector :as v]
            [basic-particles.protocols.particle-operations :refer :all])
  (:import [basic_particles.vector Vector]))

(defrecord Particle [location velocity acceleration lifespan]
  ParticleOperations
  (update-particle [p]
  	p)
  (render [p]))