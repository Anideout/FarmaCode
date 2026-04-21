-- =============================================================
-- FarmaCode - Datos de prueba (seed data)
-- Usa INSERT IGNORE para no fallar si los datos ya existen.
-- Precios referenciales en pesos chilenos (CLP).
-- =============================================================

-- -------------------------------------------------------------
-- Principios activos
-- -------------------------------------------------------------
INSERT IGNORE INTO principio_activo (nombre, descripcion, categoria) VALUES
('Paracetamol',
 'Analgésico y antipirético ampliamente utilizado. Inhibe la síntesis de prostaglandinas en el sistema nervioso central.',
 'Analgésico'),
('Ácido acetilsalicílico',
 'Antiinflamatorio no esteroideo (AINE) con propiedades analgésicas, antipiréticas y antiagregantes plaquetarias.',
 'Analgésico/Antiinflamatorio'),
('Clonazepam',
 'Benzodiazepina de acción prolongada. Potencia el efecto del GABA en el sistema nervioso central.',
 'Ansiolítico'),
('Tramadol',
 'Analgésico opiáceo de acción central, indicado para dolores moderados a severos.',
 'Analgésico opioide'),
('Omeprazol',
 'Inhibidor de la bomba de protones. Reduce la secreción de ácido gástrico.',
 'Inhibidor bomba de protones'),
('Ibuprofeno',
 'Antiinflamatorio no esteroideo (AINE). Inhibe las enzimas COX-1 y COX-2.',
 'Antiinflamatorio AINE'),
('Diclofenaco',
 'Antiinflamatorio no esteroideo (AINE) con marcada actividad antiinflamatoria y analgésica.',
 'Antiinflamatorio AINE'),
('Amoxicilina',
 'Antibiótico betalactámico de amplio espectro. Inhibe la síntesis de la pared celular bacteriana.',
 'Antibiótico');

-- -------------------------------------------------------------
-- Laboratorios chilenos y extranjeros con presencia en Chile
-- -------------------------------------------------------------
INSERT IGNORE INTO laboratorio (nombre, pais) VALUES
('Recalcine', 'Chile'),
('Mintlab', 'Chile'),
('Saval', 'Chile'),
('Bagó', 'Argentina'),
('Andrómaco', 'Chile'),
('Chile Lab', 'Chile'),
('Bayer', 'Alemania'),
('Pfizer', 'Estados Unidos'),
('GlaxoSmithKline', 'Reino Unido'),
('Roche', 'Suiza');

-- -------------------------------------------------------------
-- Medicamentos: Paracetamol
-- -------------------------------------------------------------
INSERT IGNORE INTO medicamento (nombre_comercial, dosis, presentacion, administracion, tipo, cert_isp, descripcion, principio_activo_id, laboratorio_id)
VALUES
('Tapsin',
 '500mg',
 'Comprimidos recubiertos x20',
 'Oral',
 'REFERENCIA',
 TRUE,
 'Analgésico y antipirético de referencia en el mercado chileno.',
 (SELECT id FROM principio_activo WHERE nombre = 'Paracetamol'),
 (SELECT id FROM laboratorio WHERE nombre = 'Chile Lab')),

('Kitadol',
 '500mg',
 'Comprimidos x20',
 'Oral',
 'BIOEQUIVALENTE',
 TRUE,
 'Medicamento bioequivalente certificado por el ISP.',
 (SELECT id FROM principio_activo WHERE nombre = 'Paracetamol'),
 (SELECT id FROM laboratorio WHERE nombre = 'Recalcine')),

('Paracetamol Mintlab',
 '500mg',
 'Comprimidos x20',
 'Oral',
 'GENERICO',
 FALSE,
 'Paracetamol genérico de bajo costo.',
 (SELECT id FROM principio_activo WHERE nombre = 'Paracetamol'),
 (SELECT id FROM laboratorio WHERE nombre = 'Mintlab')),

('Panadol',
 '500mg',
 'Comprimidos x20',
 'Oral',
 'REFERENCIA',
 TRUE,
 'Paracetamol de laboratorio GlaxoSmithKline, ampliamente reconocido.',
 (SELECT id FROM principio_activo WHERE nombre = 'Paracetamol'),
 (SELECT id FROM laboratorio WHERE nombre = 'GlaxoSmithKline'));

-- -------------------------------------------------------------
-- Medicamentos: Ácido acetilsalicílico
-- -------------------------------------------------------------
INSERT IGNORE INTO medicamento (nombre_comercial, dosis, presentacion, administracion, tipo, cert_isp, descripcion, principio_activo_id, laboratorio_id)
VALUES
('Aspirina',
 '500mg',
 'Comprimidos x20',
 'Oral',
 'REFERENCIA',
 TRUE,
 'Medicamento de referencia mundial para ácido acetilsalicílico.',
 (SELECT id FROM principio_activo WHERE nombre = 'Ácido acetilsalicílico'),
 (SELECT id FROM laboratorio WHERE nombre = 'Bayer')),

('Coliaspirina',
 '500mg',
 'Comprimidos efervescentes x20',
 'Oral',
 'BIOEQUIVALENTE',
 TRUE,
 'Formulación efervescente bioequivalente.',
 (SELECT id FROM principio_activo WHERE nombre = 'Ácido acetilsalicílico'),
 (SELECT id FROM laboratorio WHERE nombre = 'Recalcine')),

('AAS Saval',
 '100mg',
 'Comprimidos x30',
 'Oral',
 'GENERICO',
 FALSE,
 'Dosis baja para uso cardiovascular preventivo.',
 (SELECT id FROM principio_activo WHERE nombre = 'Ácido acetilsalicílico'),
 (SELECT id FROM laboratorio WHERE nombre = 'Saval'));

-- -------------------------------------------------------------
-- Medicamentos: Clonazepam
-- -------------------------------------------------------------
INSERT IGNORE INTO medicamento (nombre_comercial, dosis, presentacion, administracion, tipo, cert_isp, descripcion, principio_activo_id, laboratorio_id)
VALUES
('Ravotril',
 '2mg',
 'Comprimidos x30',
 'Oral',
 'REFERENCIA',
 TRUE,
 'Medicamento de referencia para el tratamiento de epilepsia y trastornos de ansiedad.',
 (SELECT id FROM principio_activo WHERE nombre = 'Clonazepam'),
 (SELECT id FROM laboratorio WHERE nombre = 'Roche')),

('Clonazepam Recalcine',
 '2mg',
 'Comprimidos x30',
 'Oral',
 'BIOEQUIVALENTE',
 TRUE,
 'Clonazepam bioequivalente certificado.',
 (SELECT id FROM principio_activo WHERE nombre = 'Clonazepam'),
 (SELECT id FROM laboratorio WHERE nombre = 'Recalcine')),

('Clonazepam Mintlab',
 '0.5mg',
 'Comprimidos x30',
 'Oral',
 'GENERICO',
 FALSE,
 'Dosis reducida para inicio de tratamiento.',
 (SELECT id FROM principio_activo WHERE nombre = 'Clonazepam'),
 (SELECT id FROM laboratorio WHERE nombre = 'Mintlab'));

-- -------------------------------------------------------------
-- Medicamentos: Tramadol
-- -------------------------------------------------------------
INSERT IGNORE INTO medicamento (nombre_comercial, dosis, presentacion, administracion, tipo, cert_isp, descripcion, principio_activo_id, laboratorio_id)
VALUES
('Zaldiar',
 '37.5mg/325mg',
 'Comprimidos recubiertos x10',
 'Oral',
 'REFERENCIA',
 TRUE,
 'Combinación de tramadol y paracetamol para dolor moderado a severo.',
 (SELECT id FROM principio_activo WHERE nombre = 'Tramadol'),
 (SELECT id FROM laboratorio WHERE nombre = 'Pfizer')),

('Tramadol Andrómaco',
 '50mg',
 'Cápsulas x10',
 'Oral',
 'GENERICO',
 FALSE,
 'Tramadol genérico para manejo del dolor.',
 (SELECT id FROM principio_activo WHERE nombre = 'Tramadol'),
 (SELECT id FROM laboratorio WHERE nombre = 'Andrómaco')),

('Tramadol Saval',
 '100mg',
 'Comprimidos de liberación prolongada x10',
 'Oral',
 'BIOEQUIVALENTE',
 TRUE,
 'Formulación de liberación controlada para dolor crónico.',
 (SELECT id FROM principio_activo WHERE nombre = 'Tramadol'),
 (SELECT id FROM laboratorio WHERE nombre = 'Saval'));

-- -------------------------------------------------------------
-- Medicamentos: Omeprazol
-- -------------------------------------------------------------
INSERT IGNORE INTO medicamento (nombre_comercial, dosis, presentacion, administracion, tipo, cert_isp, descripcion, principio_activo_id, laboratorio_id)
VALUES
('Losec',
 '20mg',
 'Cápsulas x28',
 'Oral',
 'REFERENCIA',
 TRUE,
 'Medicamento de referencia para úlcera péptica y reflujo gastroesofágico.',
 (SELECT id FROM principio_activo WHERE nombre = 'Omeprazol'),
 (SELECT id FROM laboratorio WHERE nombre = 'GlaxoSmithKline')),

('Omeprazol Recalcine',
 '20mg',
 'Cápsulas x28',
 'Oral',
 'BIOEQUIVALENTE',
 TRUE,
 'Omeprazol bioequivalente de menor costo.',
 (SELECT id FROM principio_activo WHERE nombre = 'Omeprazol'),
 (SELECT id FROM laboratorio WHERE nombre = 'Recalcine')),

('Omeprazol Mintlab',
 '20mg',
 'Cápsulas x14',
 'Oral',
 'GENERICO',
 FALSE,
 'Omeprazol genérico económico.',
 (SELECT id FROM principio_activo WHERE nombre = 'Omeprazol'),
 (SELECT id FROM laboratorio WHERE nombre = 'Mintlab'));

-- -------------------------------------------------------------
-- Medicamentos: Ibuprofeno
-- -------------------------------------------------------------
INSERT IGNORE INTO medicamento (nombre_comercial, dosis, presentacion, administracion, tipo, cert_isp, descripcion, principio_activo_id, laboratorio_id)
VALUES
('Brufen',
 '400mg',
 'Comprimidos recubiertos x20',
 'Oral',
 'REFERENCIA',
 TRUE,
 'Antiinflamatorio de referencia, ampliamente utilizado para dolor e inflamación.',
 (SELECT id FROM principio_activo WHERE nombre = 'Ibuprofeno'),
 (SELECT id FROM laboratorio WHERE nombre = 'Bagó')),

('Ibuprofeno Saval',
 '400mg',
 'Comprimidos x20',
 'Oral',
 'BIOEQUIVALENTE',
 TRUE,
 'Ibuprofeno bioequivalente certificado por ISP.',
 (SELECT id FROM principio_activo WHERE nombre = 'Ibuprofeno'),
 (SELECT id FROM laboratorio WHERE nombre = 'Saval')),

('Ibuprofeno Andrómaco',
 '200mg',
 'Comprimidos x20',
 'Oral',
 'GENERICO',
 FALSE,
 'Ibuprofeno genérico en dosis baja para adultos mayores.',
 (SELECT id FROM principio_activo WHERE nombre = 'Ibuprofeno'),
 (SELECT id FROM laboratorio WHERE nombre = 'Andrómaco'));

-- -------------------------------------------------------------
-- Medicamentos: Diclofenaco
-- -------------------------------------------------------------
INSERT IGNORE INTO medicamento (nombre_comercial, dosis, presentacion, administracion, tipo, cert_isp, descripcion, principio_activo_id, laboratorio_id)
VALUES
('Voltaren',
 '50mg',
 'Comprimidos recubiertos x20',
 'Oral',
 'REFERENCIA',
 TRUE,
 'Diclofenaco de referencia para dolor musculoesquelético.',
 (SELECT id FROM principio_activo WHERE nombre = 'Diclofenaco'),
 (SELECT id FROM laboratorio WHERE nombre = 'Pfizer')),

('Diclofenaco Recalcine',
 '50mg',
 'Comprimidos x20',
 'Oral',
 'BIOEQUIVALENTE',
 TRUE,
 'Diclofenaco bioequivalente de precio accesible.',
 (SELECT id FROM principio_activo WHERE nombre = 'Diclofenaco'),
 (SELECT id FROM laboratorio WHERE nombre = 'Recalcine')),

('Diclofenaco Saval',
 '75mg',
 'Solución inyectable x3 ampollas',
 'Intramuscular',
 'GENERICO',
 FALSE,
 'Formulación inyectable para dolor agudo.',
 (SELECT id FROM principio_activo WHERE nombre = 'Diclofenaco'),
 (SELECT id FROM laboratorio WHERE nombre = 'Saval'));

-- -------------------------------------------------------------
-- Medicamentos: Amoxicilina
-- -------------------------------------------------------------
INSERT IGNORE INTO medicamento (nombre_comercial, dosis, presentacion, administracion, tipo, cert_isp, descripcion, principio_activo_id, laboratorio_id)
VALUES
('Amoxil',
 '500mg',
 'Cápsulas x21',
 'Oral',
 'REFERENCIA',
 TRUE,
 'Antibiótico de referencia para infecciones bacterianas.',
 (SELECT id FROM principio_activo WHERE nombre = 'Amoxicilina'),
 (SELECT id FROM laboratorio WHERE nombre = 'GlaxoSmithKline')),

('Amoxicilina Mintlab',
 '500mg',
 'Cápsulas x21',
 'Oral',
 'BIOEQUIVALENTE',
 TRUE,
 'Amoxicilina bioequivalente de menor costo.',
 (SELECT id FROM principio_activo WHERE nombre = 'Amoxicilina'),
 (SELECT id FROM laboratorio WHERE nombre = 'Mintlab')),

('Amoxicilina Andrómaco',
 '250mg/5ml',
 'Suspensión oral 100ml',
 'Oral',
 'GENERICO',
 FALSE,
 'Formulación pediátrica en suspensión.',
 (SELECT id FROM principio_activo WHERE nombre = 'Amoxicilina'),
 (SELECT id FROM laboratorio WHERE nombre = 'Andrómaco'));

-- =============================================================
-- PRECIOS (valores referenciales en CLP - pesos chilenos)
-- vigente = 1 indica el precio actualmente válido
-- =============================================================

-- --- Paracetamol ---
-- Tapsin (~$2.500)
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Tapsin'), 2490.00, 'Farmacias Ahumada', TRUE);

-- Kitadol (~$1.800)
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Kitadol'), 1790.00, 'Cruz Verde', TRUE);

-- Paracetamol Mintlab (~$800)
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Paracetamol Mintlab'), 790.00, 'Salcobrand', TRUE);

-- Panadol (~$3.200)
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Panadol'), 3190.00, 'Farmacias Ahumada', TRUE);

-- --- Ácido acetilsalicílico ---
-- Aspirina (~$2.800)
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Aspirina'), 2790.00, 'Farmacias Ahumada', TRUE);

-- Coliaspirina (~$1.900)
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Coliaspirina'), 1890.00, 'Cruz Verde', TRUE);

-- AAS Saval (~$1.200)
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'AAS Saval'), 1190.00, 'Salcobrand', TRUE);

-- --- Clonazepam ---
-- Ravotril (~$5.500)
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Ravotril'), 5490.00, 'Farmacias Ahumada', TRUE);

-- Clonazepam Recalcine (~$2.200)
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Clonazepam Recalcine'), 2190.00, 'Cruz Verde', TRUE);

-- Clonazepam Mintlab (~$1.500)
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Clonazepam Mintlab'), 1490.00, 'Salcobrand', TRUE);

-- --- Tramadol ---
-- Zaldiar (~$7.800)
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Zaldiar'), 7790.00, 'Farmacias Ahumada', TRUE);

-- Tramadol Andrómaco (~$3.500)
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Tramadol Andrómaco'), 3490.00, 'Cruz Verde', TRUE);

-- Tramadol Saval (~$4.200)
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Tramadol Saval'), 4190.00, 'Salcobrand', TRUE);

-- --- Omeprazol ---
-- Losec (~$8.900)
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Losec'), 8890.00, 'Farmacias Ahumada', TRUE);

-- Omeprazol Recalcine (~$2.900)
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Omeprazol Recalcine'), 2890.00, 'Cruz Verde', TRUE);

-- Omeprazol Mintlab (~$1.400)
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Omeprazol Mintlab'), 1390.00, 'Salcobrand', TRUE);

-- --- Ibuprofeno ---
-- Brufen (~$3.600)
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Brufen'), 3590.00, 'Farmacias Ahumada', TRUE);

-- Ibuprofeno Saval (~$1.600)
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Ibuprofeno Saval'), 1590.00, 'Cruz Verde', TRUE);

-- Ibuprofeno Andrómaco (~$990)
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Ibuprofeno Andrómaco'), 990.00, 'Salcobrand', TRUE);

-- --- Diclofenaco ---
-- Voltaren (~$4.800)
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Voltaren'), 4790.00, 'Farmacias Ahumada', TRUE);

-- Diclofenaco Recalcine (~$1.800)
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Diclofenaco Recalcine'), 1790.00, 'Cruz Verde', TRUE);

-- Diclofenaco Saval inyectable (~$3.200)
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Diclofenaco Saval'), 3190.00, 'Salcobrand', TRUE);

-- --- Amoxicilina ---
-- Amoxil (~$6.500)
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Amoxil'), 6490.00, 'Farmacias Ahumada', TRUE);

-- Amoxicilina Mintlab (~$2.400)
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Amoxicilina Mintlab'), 2390.00, 'Cruz Verde', TRUE);

-- Amoxicilina Andrómaco pediátrica (~$3.800)
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Amoxicilina Andrómaco'), 3790.00, 'Salcobrand', TRUE);

-- =============================================================
-- Usuario de prueba (ID=1 - usado por HistorialController)
-- password_hash corresponde a 'password123' en BCrypt (solo para pruebas)
-- =============================================================
INSERT IGNORE INTO usuario (nombre, email, password_hash, activo)
VALUES ('Usuario Demo', 'demo@farmacode.cl',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        TRUE);
