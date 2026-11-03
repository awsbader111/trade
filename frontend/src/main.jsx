import React, { useEffect, useState } from 'react';
import { createRoot } from 'react-dom/client';
import axios from 'axios';
import './styles.css';

const empty = { firstName: '', lastName: '', email: '', program: '', credits: 0 };
const api = axios.create({ baseURL: '/api/students', timeout: 12000 });

function App() {
  const [students, setStudents] = useState([]);
  const [form, setForm] = useState(empty);
  const [editId, setEditId] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [filter, setFilter] = useState('');

  const load = async () => {
    setLoading(true);
    try { const { data } = await api.get(''); setStudents(data); setError(''); }
    catch (e) { setError(explain(e)); }
    finally { setLoading(false); }
  };
  useEffect(() => { load(); }, []);

  function explain(e) {
    if (e.response?.status === 400) return 'Please check the required fields, email address and credits (0–300).';
    if (e.response?.status === 404) return 'Record was not found. Please refresh.';
    return `Request failed: ${e.message || 'unknown error'}`;
  }
  function reset() { setForm(empty); setEditId(null); }
  function beginEdit(student) {
    setForm({ firstName: student.firstName, lastName: student.lastName, email: student.email,
      program: student.program, credits: student.credits });
    setEditId(student.id); setError(''); setMessage(''); window.scrollTo({top:0,behavior:'smooth'});
  }
  async function save(event) {
    event.preventDefault(); setSaving(true); setError(''); setMessage('');
    const body = {...form, credits: Number(form.credits)};
    try {
      if (editId == null) { await api.post('',body); setMessage('Student created.'); }
      else { await api.put(`/${editId}`,body); setMessage('Student updated.'); }
      reset(); await load();
    } catch (e) { setError(explain(e)); }
    finally { setSaving(false); }
  }
  async function remove(student) {
    if (!window.confirm(`Delete ${student.firstName} ${student.lastName}?`)) return;
    setError(''); setMessage('');
    try { await api.delete(`/${student.id}`); setMessage('Student deleted.'); if (editId===student.id) reset(); await load(); }
    catch (e) { setError(explain(e)); }
  }
  const displayed = students.filter(s => `${s.firstName} ${s.lastName} ${s.program} ${s.email}`.toLowerCase().includes(filter.toLowerCase()));
  return <>
    <header className="topbar"><strong>AtlasFX · Currency Intelligence</strong><nav aria-label="Main navigation">
      <a href="/">Home</a><a href="/exercise">SOAP · MNB</a><a href="/account_info">Forex Account</a><a href="/actual_prices">Actual Prices</a><a href="/hist_prices">Historical Prices</a><a href="/open_position">Open Trade</a><a href="/positions">Open Positions</a><a href="/close_position">Close Trade</a><a href="/test">Test · Calculator</a><a href="/react/index.html" aria-current="page">React · Students</a>
    </nav></header>
    <main className="content">
      <div className="eyebrow">React + Axios + Spring Boot + H2</div>
      <h1>Student Management</h1><p>Create, view, edit and remove records. Data is stored in a file-backed H2 database on the server.</p>
      {error && <div className="alert" role="alert">{error}</div>}
      {message && <div className="success" role="status">{message}</div>}
      <div className="layout">
        <section className="panel"><h2>{editId===null?'Add student':'Edit student'}</h2>
          <form onSubmit={save}>
            <label>First name<input required maxLength="70" value={form.firstName} onChange={e=>setForm({...form,firstName:e.target.value})}/></label>
            <label>Last name<input required maxLength="70" value={form.lastName} onChange={e=>setForm({...form,lastName:e.target.value})}/></label>
            <label>Email<input type="email" required maxLength="160" value={form.email} onChange={e=>setForm({...form,email:e.target.value})}/></label>
            <label>Program<input required maxLength="80" value={form.program} onChange={e=>setForm({...form,program:e.target.value})}/></label>
            <label>Credits<input type="number" min="0" max="300" required value={form.credits} onChange={e=>setForm({...form,credits:e.target.value})}/></label>
            <div className="buttons"><button disabled={saving} type="submit">{saving?'Saving…':editId===null?'Create student':'Save changes'}</button>{editId!==null && <button type="button" className="secondary" onClick={reset}>Cancel</button>}</div>
          </form>
        </section>
        <section className="panel records"><div className="section-head"><div><h2>Students</h2><span>{students.length} records</span></div><button className="secondary" type="button" onClick={load}>Refresh</button></div>
          <label className="filter">Search students<input value={filter} onChange={e=>setFilter(e.target.value)} placeholder="Name, program or email"/></label>
          {loading?<p role="status">Loading students…</p>:displayed.length===0?<p>No matching students. Add your first record using the form.</p>:
          <div className="table-wrap"><table><thead><tr><th>ID</th><th>Student</th><th>Email</th><th>Program</th><th>Credits</th><th>Actions</th></tr></thead><tbody>{displayed.map(s=><tr key={s.id}><td>{s.id}</td><td><b>{s.firstName} {s.lastName}</b></td><td>{s.email}</td><td>{s.program}</td><td>{s.credits}</td><td><div className="buttons"><button type="button" className="secondary" onClick={()=>beginEdit(s)}>Edit</button><button type="button" className="danger" onClick={()=>remove(s)}>Delete</button></div></td></tr>)}</tbody></table></div>}
        </section>
      </div>
    </main>
    <footer className="footer">Java Applications – Lecture Homework · Salokhov Jahongir — PQJEK6 &amp; Bader Aws Basel Saleem — ZUKGOB · John von Neumann University</footer>
  </>;
}

createRoot(document.getElementById('root')).render(<React.StrictMode><App/></React.StrictMode>);
