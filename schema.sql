CREATE TABLE produtos (
                          id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          nome VARCHAR(120) NOT NULL CHECK (length(trim(nome)) > 0),
                          preco NUMERIC(12, 2) NOT NULL CHECK (preco >= 0),
                          quantidade_estoque INTEGER NOT NULL CHECK (quantidade_estoque >= 0),
                          estoque_minimo INTEGER NOT NULL CHECK (estoque_minimo >= 0)
);

CREATE TABLE vendas (
                        id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        data TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        total NUMERIC(24, 2) NOT NULL CHECK (total >= 0)
);

CREATE TABLE itens_venda (
                             id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                             venda_id INTEGER NOT NULL REFERENCES vendas(id),
                             produto_id INTEGER NOT NULL REFERENCES produtos(id),
                             nome_produto VARCHAR(120) NOT NULL,
                             quantidade INTEGER NOT NULL CHECK (quantidade > 0),
                             preco_unitario NUMERIC(12, 2) NOT NULL CHECK (preco_unitario >= 0),
                             UNIQUE (venda_id, produto_id)
);