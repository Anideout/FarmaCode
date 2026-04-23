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
 'Analgésico/Antipirético'),
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
 'Inhibidor de la bomba de protones. Reduce la secreción de ácido gástrico. Indicado para úlceras gástricas, duodenales y reflujo gastroesofágico.',
 'Antiulceroso'),
('Ibuprofeno',
 'Antiinflamatorio no esteroideo (AINE). Tiene propiedades analgésicas, antiinflamatorias y antipiréticas. Inhibe las enzimas COX-1 y COX-2.',
 'Antiinflamatorio'),
('Diclofenaco',
 'Antiinflamatorio no esteroideo (AINE) con marcada actividad antiinflamatoria y analgésica.',
 'Antiinflamatorio AINE'),
('Amoxicilina',
 'Antibiótico betalactámico de amplio espectro. Inhibe la síntesis de la pared celular bacteriana.',
 'Antibiótico'),
('Sertralina',
 'Antidepresivo inhibidor selectivo de la recaptación de serotonina (ISRS). Se utiliza para tratar la depresión, trastorno de pánico, trastorno de ansiedad social y trastorno obsesivo-compulsivo.',
 'Antidepresivo'),
('Atorvastatina',
 'Estatina que reduce el colesterol LDL y los triglicéridos. Disminuye el riesgo de enfermedades cardiovasculares en pacientes con hipercolesterolemia.',
 'Hipolipemiante'),
('Losartán',
 'Antagonista de los receptores de angiotensina II. Se utiliza para tratar la hipertensión arterial y proteger los riñones en pacientes diabéticos.',
 'Antihipertensivo'),
('Enalapril',
 'Inhibidor de la enzima convertidora de angiotensina (IECA). Trata la hipertensión y la insuficiencia cardíaca congestiva.',
 'Antihipertensivo'),
('Metformina',
 'Antidiabético oral biguanida. Primera línea de tratamiento para diabetes tipo 2. Mejora la sensibilidad a la insulina y reduce la producción hepática de glucosa.',
 'Antidiabético');

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
('Chile Labs', 'Chile'),
('Chile Laboratorios', 'Chile'),
('Chile Pharma', 'Chile'),
('Chile Med', 'Chile'),
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

-- -------------------------------------------------------------
-- Medicamentos: Sertralina
-- -------------------------------------------------------------
INSERT IGNORE INTO medicamento (nombre_comercial, dosis, presentacion, administracion, tipo, cert_isp, descripcion, principio_activo_id, laboratorio_id)
VALUES
('Sertralina',
 '50mg',
 '28 comprimidos',
 'Oral',
 'GENERICO',
 TRUE,
 'Antidepresivo ISRS genérico certificado por el ISP.',
 (SELECT id FROM principio_activo WHERE nombre = 'Sertralina'),
 (SELECT id FROM laboratorio WHERE nombre = 'Chile Laboratorios')),

('Sertralina Ref',
 '50mg',
 '28 comprimidos',
 'Oral',
 'REFERENCIA',
 TRUE,
 'Medicamento de referencia para Sertralina. Antidepresivo ISRS de primera línea con amplia evidencia clínica.',
 (SELECT id FROM principio_activo WHERE nombre = 'Sertralina'),
 (SELECT id FROM laboratorio WHERE nombre = 'Pfizer')),

('Sertralina Recalcine',
 '50mg',
 '28 comprimidos',
 'Oral',
 'BIOEQUIVALENTE',
 TRUE,
 'Sertralina bioequivalente certificada por el ISP. Misma eficacia y seguridad que el medicamento de referencia.',
 (SELECT id FROM principio_activo WHERE nombre = 'Sertralina'),
 (SELECT id FROM laboratorio WHERE nombre = 'Recalcine'));

-- -------------------------------------------------------------
-- Medicamentos: Atorvastatina
-- -------------------------------------------------------------
INSERT IGNORE INTO medicamento (nombre_comercial, dosis, presentacion, administracion, tipo, cert_isp, descripcion, principio_activo_id, laboratorio_id)
VALUES
('Atorvastatina',
 '20mg',
 '30 comprimidos',
 'Oral',
 'GENERICO',
 TRUE,
 'Estatina genérica para reducción de colesterol LDL.',
 (SELECT id FROM principio_activo WHERE nombre = 'Atorvastatina'),
 (SELECT id FROM laboratorio WHERE nombre = 'Chile Lab')),

('AtorvaBio',
 '20mg',
 '30 comprimidos',
 'Oral',
 'BIOEQUIVALENTE',
 TRUE,
 'Estatina bioequivalente. Demuestra equivalencia terapéutica con el medicamento de referencia. Misma eficacia y seguridad.',
 (SELECT id FROM principio_activo WHERE nombre = 'Atorvastatina'),
 (SELECT id FROM laboratorio WHERE nombre = 'Chile Pharma')),

('Lipitor',
 '20mg',
 '30 comprimidos',
 'Oral',
 'REFERENCIA',
 TRUE,
 'Medicamento de referencia para Atorvastatina. Amplia evidencia en reducción de riesgo cardiovascular.',
 (SELECT id FROM principio_activo WHERE nombre = 'Atorvastatina'),
 (SELECT id FROM laboratorio WHERE nombre = 'Pfizer'));

-- -------------------------------------------------------------
-- Medicamentos: Losartán
-- -------------------------------------------------------------
INSERT IGNORE INTO medicamento (nombre_comercial, dosis, presentacion, administracion, tipo, cert_isp, descripcion, principio_activo_id, laboratorio_id)
VALUES
('Losartán',
 '50mg',
 '30 comprimidos',
 'Oral',
 'GENERICO',
 TRUE,
 'Antihipertensivo ARA-II genérico certificado por el ISP.',
 (SELECT id FROM principio_activo WHERE nombre = 'Losartán'),
 (SELECT id FROM laboratorio WHERE nombre = 'Chile Labs')),

('Cozaar',
 '50mg',
 '28 comprimidos',
 'Oral',
 'REFERENCIA',
 TRUE,
 'Medicamento de referencia para Losartán. Tratamiento de primera línea para hipertensión arterial.',
 (SELECT id FROM principio_activo WHERE nombre = 'Losartán'),
 (SELECT id FROM laboratorio WHERE nombre = 'GlaxoSmithKline')),

('Losartán Recalcine',
 '50mg',
 '30 comprimidos',
 'Oral',
 'BIOEQUIVALENTE',
 TRUE,
 'Losartán bioequivalente certificado por el ISP.',
 (SELECT id FROM principio_activo WHERE nombre = 'Losartán'),
 (SELECT id FROM laboratorio WHERE nombre = 'Recalcine'));

-- -------------------------------------------------------------
-- Medicamentos: Enalapril
-- -------------------------------------------------------------
INSERT IGNORE INTO medicamento (nombre_comercial, dosis, presentacion, administracion, tipo, cert_isp, descripcion, principio_activo_id, laboratorio_id)
VALUES
('Enalapril',
 '10mg',
 '30 comprimidos',
 'Oral',
 'GENERICO',
 TRUE,
 'IECA genérico para tratamiento de hipertensión arterial e insuficiencia cardíaca.',
 (SELECT id FROM principio_activo WHERE nombre = 'Enalapril'),
 (SELECT id FROM laboratorio WHERE nombre = 'Chile Labs')),

('Renitec',
 '10mg',
 '28 comprimidos',
 'Oral',
 'REFERENCIA',
 TRUE,
 'Medicamento de referencia para Enalapril. Ampliamente utilizado en hipertensión e insuficiencia cardíaca.',
 (SELECT id FROM principio_activo WHERE nombre = 'Enalapril'),
 (SELECT id FROM laboratorio WHERE nombre = 'GlaxoSmithKline')),

('Enalapril Mintlab',
 '5mg',
 '30 comprimidos',
 'Oral',
 'BIOEQUIVALENTE',
 TRUE,
 'Enalapril bioequivalente en dosis inicial para pacientes nuevos.',
 (SELECT id FROM principio_activo WHERE nombre = 'Enalapril'),
 (SELECT id FROM laboratorio WHERE nombre = 'Mintlab'));

-- -------------------------------------------------------------
-- Medicamentos: Metformina
-- -------------------------------------------------------------
INSERT IGNORE INTO medicamento (nombre_comercial, dosis, presentacion, administracion, tipo, cert_isp, descripcion, principio_activo_id, laboratorio_id)
VALUES
('Metformina',
 '850mg',
 '30 comprimidos',
 'Oral',
 'GENERICO',
 TRUE,
 'Antidiabético oral de primera línea para diabetes tipo 2.',
 (SELECT id FROM principio_activo WHERE nombre = 'Metformina'),
 (SELECT id FROM laboratorio WHERE nombre = 'Chile Med')),

('Glucophage',
 '850mg',
 '30 comprimidos',
 'Oral',
 'REFERENCIA',
 TRUE,
 'Medicamento de referencia para Metformina. Primera línea en el tratamiento de diabetes tipo 2.',
 (SELECT id FROM principio_activo WHERE nombre = 'Metformina'),
 (SELECT id FROM laboratorio WHERE nombre = 'GlaxoSmithKline')),

('Metformina Saval',
 '500mg',
 '30 comprimidos',
 'Oral',
 'BIOEQUIVALENTE',
 TRUE,
 'Metformina bioequivalente en dosis inicial para pacientes nuevos o con menor tolerancia.',
 (SELECT id FROM principio_activo WHERE nombre = 'Metformina'),
 (SELECT id FROM laboratorio WHERE nombre = 'Saval'));

-- -------------------------------------------------------------
-- Medicamento adicional: Aspirina Protect (Ácido acetilsalicílico - baja dosis)
-- -------------------------------------------------------------
INSERT IGNORE INTO medicamento (nombre_comercial, dosis, presentacion, administracion, tipo, cert_isp, descripcion, principio_activo_id, laboratorio_id)
VALUES
('Aspirina Protect',
 '100mg',
 '30 comprimidos',
 'Oral',
 'REFERENCIA',
 TRUE,
 'Antiinflamatorio y antiagregante plaquetario. Baja dosis para prevención cardiovascular. Alta dosis para dolor y fiebre.',
 (SELECT id FROM principio_activo WHERE nombre = 'Ácido acetilsalicílico'),
 (SELECT id FROM laboratorio WHERE nombre = 'Bayer'));

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

-- --- Sertralina ---
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Sertralina'), 3490.00, 'Cruz Verde', TRUE);
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Sertralina Ref'), 12990.00, 'Farmacias Ahumada', TRUE);
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Sertralina Recalcine'), 4290.00, 'Salcobrand', TRUE);

-- --- Atorvastatina ---
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Atorvastatina'), 2990.00, 'Cruz Verde', TRUE);
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'AtorvaBio'), 3490.00, 'Salcobrand', TRUE);
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Lipitor'), 14990.00, 'Farmacias Ahumada', TRUE);

-- --- Losartán ---
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Losartán'), 2190.00, 'Cruz Verde', TRUE);
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Cozaar'), 11990.00, 'Farmacias Ahumada', TRUE);
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Losartán Recalcine'), 2890.00, 'Salcobrand', TRUE);

-- --- Enalapril ---
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Enalapril'), 1390.00, 'Salcobrand', TRUE);
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Renitec'), 8990.00, 'Farmacias Ahumada', TRUE);
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Enalapril Mintlab'), 1190.00, 'Cruz Verde', TRUE);

-- --- Metformina ---
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Metformina'), 1990.00, 'Cruz Verde', TRUE);
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Glucophage'), 9990.00, 'Farmacias Ahumada', TRUE);
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Metformina Saval'), 1590.00, 'Salcobrand', TRUE);

-- --- Aspirina Protect ---
INSERT IGNORE INTO precio (medicamento_id, valor, fuente, vigente)
VALUES ((SELECT id FROM medicamento WHERE nombre_comercial = 'Aspirina Protect'), 3990.00, 'Farmacias Ahumada', TRUE);

-- =============================================================
-- Usuario de prueba (ID=1 - usado por HistorialController)
-- password_hash corresponde a 'password123' en BCrypt (solo para pruebas)
-- =============================================================
INSERT IGNORE INTO usuario (nombre, email, password_hash, activo)
VALUES ('Usuario Demo', 'demo@farmacode.cl',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        TRUE);
